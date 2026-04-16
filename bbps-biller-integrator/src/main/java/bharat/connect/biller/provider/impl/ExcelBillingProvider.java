package bharat.connect.biller.provider.impl;

import bharat.connect.biller.model.BillDetails;
import bharat.connect.biller.provider.BillingProvider;
import bharat.connect.biller.provider.BillingProviderProperties;
import bharat.connect.biller.provider.CustomerParamCriterion;
import bharat.connect.biller.provider.PaymentUpdateRequest;
import bharat.connect.biller.provider.ProviderSupport;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExcelBillingProvider implements BillingProvider {
    private static final String UNPAID = "UNPAID";
    private static final String PAID = "PAID";

    private final BillingProviderProperties properties;
    private final DateTimeFormatter dateFormatter;
    private final DataFormatter dataFormatter = new DataFormatter();

    public ExcelBillingProvider(BillingProviderProperties properties) {
        this.properties = properties;
        this.dateFormatter = DateTimeFormatter.ofPattern(properties.getDateFormat());
    }

    @Override
    public BillDetails findLatestUnpaidBill(List<CustomerParamCriterion> criteria) {
        List<ExcelRow> rows = readRows();
        return rows.stream()
                .map(ExcelRow::toBillDetails)
                .filter(b -> UNPAID.equalsIgnoreCase(b.getBillStatus()))
                .filter(b -> ProviderSupport.matches(criteria, b))
                .sorted(ProviderSupport.latestUnpaidOrder())
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean markBillPaidAndRecordTransaction(List<CustomerParamCriterion> criteria, PaymentUpdateRequest paymentUpdateRequest) {
        List<ExcelRow> rows = readRows();
        Optional<ExcelRow> latest = rows.stream()
                .filter(r -> UNPAID.equalsIgnoreCase(r.status))
                .filter(r -> ProviderSupport.matches(criteria, r.toBillDetails()))
                .sorted((a, b) -> ProviderSupport.latestUnpaidOrder().compare(a.toBillDetails(), b.toBillDetails()))
                .findFirst();
        if (latest.isEmpty()) {
            return false;
        }
        latest.get().status = PAID;
        writeRows(rows);
        appendPaymentLog(paymentUpdateRequest);
        return true;
    }

    private List<ExcelRow> readRows() {
        Path path = Paths.get(properties.getExcel().getPath());
        if (!Files.exists(path)) {
            return List.of();
        }
        try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ);
             Workbook workbook = new XSSFWorkbook(in)) {
            Sheet sheet = workbook.getSheet(properties.getExcel().getSheetName());
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                return List.of();
            }
            Row header = sheet.getRow(0);
            Map<String, Integer> indexByHeader = new HashMap<>();
            for (Cell cell : header) {
                indexByHeader.put(cell.getStringCellValue(), cell.getColumnIndex());
            }
            List<ExcelRow> rows = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                rows.add(ExcelRow.from(row, indexByHeader, properties.getExcel().getColumns(), dateFormatter, dataFormatter));
            }
            return rows;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read excel provider file", e);
        }
    }

    private void writeRows(List<ExcelRow> rows) {
        Path path = Paths.get(properties.getExcel().getPath());
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(properties.getExcel().getSheetName());
            BillingProviderProperties.Columns columns = properties.getExcel().getColumns();
            Row header = sheet.createRow(0);
            String[] headerValues = {
                    columns.getBillId(),
                    columns.getCustomerParamName(),
                    columns.getCustomerParamType(),
                    columns.getCustomerParamValue(),
                    columns.getBillAmount(),
                    columns.getBillDate(),
                    columns.getDueDate(),
                    columns.getBillNumber(),
                    columns.getBillPeriod(),
                    columns.getBillStatus()
            };
            for (int i = 0; i < headerValues.length; i++) {
                header.createCell(i).setCellValue(headerValues[i]);
            }

            int rowIndex = 1;
            for (ExcelRow row : rows) {
                Row xlsRow = sheet.createRow(rowIndex++);
                row.writeTo(xlsRow, dateFormatter);
            }
            try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                workbook.write(out);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write excel provider file", e);
        }
    }

    private void appendPaymentLog(PaymentUpdateRequest paymentUpdateRequest) {
        String paymentLogPath = properties.getExcel().getPaymentLogPath();
        if (paymentLogPath == null || paymentLogPath.isBlank()) {
            return;
        }
        Path path = Paths.get(paymentLogPath);
        try {
            if (!Files.exists(path)) {
                Files.write(path, List.of("bill_id,bbps_txn_ref,amount_paid,payment_mode"), StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            }
            String line = String.format("%s,%s,%s,%s",
                    paymentUpdateRequest.getBillId(),
                    paymentUpdateRequest.getBbpsTxnRef(),
                    paymentUpdateRequest.getAmountPaid(),
                    paymentUpdateRequest.getPaymentMode() == null ? "" : paymentUpdateRequest.getPaymentMode());
            Files.write(path, List.of(line), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to append excel payment log", e);
        }
    }

    private static class ExcelRow {
        private Long billId;
        private String customerParamName;
        private String customerParamType;
        private String customerParamValue;
        private BigDecimal billAmount;
        private LocalDate billDate;
        private LocalDate dueDate;
        private String billNumber;
        private String billPeriod;
        private String status;

        static ExcelRow from(Row row,
                             Map<String, Integer> indexByHeader,
                             BillingProviderProperties.Columns columns,
                             DateTimeFormatter dateFormatter,
                             DataFormatter dataFormatter) {
            ExcelRow data = new ExcelRow();
            data.billId = parseLong(getCellString(row, indexByHeader.get(columns.getBillId()), dataFormatter));
            data.customerParamName = getCellString(row, indexByHeader.get(columns.getCustomerParamName()), dataFormatter);
            data.customerParamType = getCellString(row, indexByHeader.get(columns.getCustomerParamType()), dataFormatter);
            data.customerParamValue = getCellString(row, indexByHeader.get(columns.getCustomerParamValue()), dataFormatter);
            data.billAmount = parseBigDecimal(getCellString(row, indexByHeader.get(columns.getBillAmount()), dataFormatter));
            data.billDate = parseDate(getCellString(row, indexByHeader.get(columns.getBillDate()), dataFormatter), dateFormatter);
            data.dueDate = parseDate(getCellString(row, indexByHeader.get(columns.getDueDate()), dataFormatter), dateFormatter);
            data.billNumber = getCellString(row, indexByHeader.get(columns.getBillNumber()), dataFormatter);
            data.billPeriod = getCellString(row, indexByHeader.get(columns.getBillPeriod()), dataFormatter);
            data.status = getCellString(row, indexByHeader.get(columns.getBillStatus()), dataFormatter);
            return data;
        }

        BillDetails toBillDetails() {
            BillDetails details = new BillDetails();
            details.setBillId(billId);
            details.setCustomerParamName(customerParamName);
            details.setCustomerParamType(customerParamType);
            details.setCustomerParamValue(customerParamValue);
            details.setBillAmount(billAmount);
            details.setBillDate(billDate);
            details.setDueDate(dueDate);
            details.setBillNumber(billNumber);
            details.setBillPeriod(billPeriod);
            details.setBillStatus(status);
            return details;
        }

        void writeTo(Row row, DateTimeFormatter formatter) {
            row.createCell(0).setCellValue(nullable(billId));
            row.createCell(1).setCellValue(nullable(customerParamName));
            row.createCell(2).setCellValue(nullable(customerParamType));
            row.createCell(3).setCellValue(nullable(customerParamValue));
            row.createCell(4).setCellValue(nullable(billAmount));
            row.createCell(5).setCellValue(formatDate(billDate, formatter));
            row.createCell(6).setCellValue(formatDate(dueDate, formatter));
            row.createCell(7).setCellValue(nullable(billNumber));
            row.createCell(8).setCellValue(nullable(billPeriod));
            row.createCell(9).setCellValue(nullable(status));
        }

        private static String getCellString(Row row, Integer index, DataFormatter dataFormatter) {
            if (index == null) {
                return null;
            }
            Cell cell = row.getCell(index);
            if (cell == null) {
                return null;
            }
            return dataFormatter.formatCellValue(cell);
        }

        private static Long parseLong(String value) {
            if (value == null || value.isBlank()) {
                return null;
            }
            return Long.parseLong(value.trim());
        }

        private static BigDecimal parseBigDecimal(String value) {
            if (value == null || value.isBlank()) {
                return null;
            }
            return new BigDecimal(value.trim());
        }

        private static LocalDate parseDate(String value, DateTimeFormatter formatter) {
            if (value == null || value.isBlank()) {
                return null;
            }
            return LocalDate.parse(value.trim(), formatter);
        }

        private static String nullable(Object value) {
            return value == null ? "" : String.valueOf(value);
        }

        private static String formatDate(LocalDate date, DateTimeFormatter formatter) {
            return date == null ? "" : formatter.format(date);
        }
    }
}
