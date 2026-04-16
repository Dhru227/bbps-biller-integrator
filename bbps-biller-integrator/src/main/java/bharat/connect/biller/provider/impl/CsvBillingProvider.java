package bharat.connect.biller.provider.impl;

import bharat.connect.biller.model.BillDetails;
import bharat.connect.biller.provider.BillingProvider;
import bharat.connect.biller.provider.BillingProviderProperties;
import bharat.connect.biller.provider.CustomerParamCriterion;
import bharat.connect.biller.provider.PaymentUpdateRequest;
import bharat.connect.biller.provider.ProviderSupport;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CsvBillingProvider implements BillingProvider {
    private static final String UNPAID = "UNPAID";
    private static final String PAID = "PAID";

    private final BillingProviderProperties properties;
    private final DateTimeFormatter dateFormatter;

    public CsvBillingProvider(BillingProviderProperties properties) {
        this.properties = properties;
        this.dateFormatter = DateTimeFormatter.ofPattern(properties.getDateFormat());
    }

    @Override
    public BillDetails findLatestUnpaidBill(List<CustomerParamCriterion> criteria) {
        List<CsvRow> rows = readRows();
        return rows.stream()
                .map(CsvRow::toBillDetails)
                .filter(b -> UNPAID.equalsIgnoreCase(b.getBillStatus()))
                .filter(b -> ProviderSupport.matches(criteria, b))
                .sorted(ProviderSupport.latestUnpaidOrder())
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean markBillPaidAndRecordTransaction(List<CustomerParamCriterion> criteria, PaymentUpdateRequest paymentUpdateRequest) {
        List<CsvRow> rows = readRows();
        Optional<CsvRow> latest = rows.stream()
                .filter(r -> UNPAID.equalsIgnoreCase(r.getStatus()))
                .filter(r -> ProviderSupport.matches(criteria, r.toBillDetails()))
                .sorted((a, b) -> ProviderSupport.latestUnpaidOrder().compare(a.toBillDetails(), b.toBillDetails()))
                .findFirst();
        if (latest.isEmpty()) {
            return false;
        }
        latest.get().setStatus(PAID);
        writeRowsAtomically(rows);
        appendPaymentLog(paymentUpdateRequest);
        return true;
    }

    private List<CsvRow> readRows() {
        Path path = Paths.get(properties.getCsv().getPath());
        if (!Files.exists(path)) {
            return List.of();
        }
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                return List.of();
            }
            String delimiter = properties.getCsv().getDelimiter();
            String[] headers = split(lines.get(0), delimiter);
            Map<String, Integer> indexByHeader = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                indexByHeader.put(headers[i].trim(), i);
            }
            List<CsvRow> rows = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                if (lines.get(i).isBlank()) {
                    continue;
                }
                String[] values = split(lines.get(i), delimiter);
                rows.add(CsvRow.from(values, indexByHeader, properties.getCsv().getColumns(), dateFormatter));
            }
            return rows;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read csv provider file", e);
        }
    }

    private void writeRowsAtomically(List<CsvRow> rows) {
        Path source = Paths.get(properties.getCsv().getPath());
        Path temp = source.resolveSibling(source.getFileName() + ".tmp");
        BillingProviderProperties.Columns columns = properties.getCsv().getColumns();
        String delimiter = properties.getCsv().getDelimiter();

        String header = String.join(delimiter,
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
        );
        List<String> lines = new ArrayList<>();
        lines.add(header);
        lines.addAll(rows.stream().map(r -> r.toCsvLine(delimiter, dateFormatter)).collect(Collectors.toList()));
        try {
            Files.write(temp, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            Files.move(temp, source, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write csv provider file", e);
        }
    }

    private void appendPaymentLog(PaymentUpdateRequest paymentUpdateRequest) {
        String paymentLogPath = properties.getCsv().getPaymentLogPath();
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
            throw new IllegalStateException("Failed to append csv payment log", e);
        }
    }

    private String[] split(String line, String delimiter) {
        return line.split(java.util.regex.Pattern.quote(delimiter), -1);
    }

    private static class CsvRow {
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

        static CsvRow from(String[] values,
                           Map<String, Integer> indexByHeader,
                           BillingProviderProperties.Columns columns,
                           DateTimeFormatter dateFormatter) {
            CsvRow row = new CsvRow();
            row.billId = parseLong(read(values, indexByHeader, columns.getBillId()));
            row.customerParamName = read(values, indexByHeader, columns.getCustomerParamName());
            row.customerParamType = read(values, indexByHeader, columns.getCustomerParamType());
            row.customerParamValue = read(values, indexByHeader, columns.getCustomerParamValue());
            row.billAmount = parseBigDecimal(read(values, indexByHeader, columns.getBillAmount()));
            row.billDate = parseDate(read(values, indexByHeader, columns.getBillDate()), dateFormatter);
            row.dueDate = parseDate(read(values, indexByHeader, columns.getDueDate()), dateFormatter);
            row.billNumber = read(values, indexByHeader, columns.getBillNumber());
            row.billPeriod = read(values, indexByHeader, columns.getBillPeriod());
            row.status = read(values, indexByHeader, columns.getBillStatus());
            return row;
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

        String toCsvLine(String delimiter, DateTimeFormatter dateFormatter) {
            return String.join(delimiter,
                    nullable(billId),
                    nullable(customerParamName),
                    nullable(customerParamType),
                    nullable(customerParamValue),
                    nullable(billAmount),
                    formatDate(billDate, dateFormatter),
                    formatDate(dueDate, dateFormatter),
                    nullable(billNumber),
                    nullable(billPeriod),
                    nullable(status));
        }

        String getStatus() {
            return status;
        }

        void setStatus(String status) {
            this.status = status;
        }

        private static String read(String[] values, Map<String, Integer> indexByHeader, String key) {
            Integer idx = indexByHeader.get(key);
            if (idx == null || idx < 0 || idx >= values.length) {
                return null;
            }
            return values[idx];
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
