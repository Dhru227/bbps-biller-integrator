package bharat.connect.biller.provider;

import bharat.connect.biller.model.BillDetails;
import bharat.connect.biller.provider.impl.CsvBillingProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvBillingProviderTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldFindLatestUnpaidAndMarkPaid() throws Exception {
        Path billsCsv = tempDir.resolve("bills.csv");
        Path paymentLog = tempDir.resolve("payments.csv");
        Files.write(
                billsCsv,
                List.of(
                        "bill_id,customer_param_name,customer_param_type,customer_param_value,bill_amount,bill_date,due_date,bill_number,bill_period,bill_status",
                        "1,mobile,STRING,9999999999,100.00,2026-04-01,2026-04-10,B-001,APR,UNPAID",
                        "2,mobile,STRING,9999999999,120.00,2026-04-05,2026-04-09,B-002,APR,UNPAID"
                ),
                StandardCharsets.UTF_8
        );

        BillingProviderProperties properties = new BillingProviderProperties();
        properties.setType("csv");
        properties.getCsv().setPath(billsCsv.toString());
        properties.getCsv().setPaymentLogPath(paymentLog.toString());
        CsvBillingProvider provider = new CsvBillingProvider(properties);

        List<CustomerParamCriterion> criteria = List.of(new CustomerParamCriterion("mobile", "9999999999"));
        BillDetails found = provider.findLatestUnpaidBill(criteria);
        assertNotNull(found);
        assertEquals(2L, found.getBillId());

        boolean updated = provider.markBillPaidAndRecordTransaction(
                criteria,
                PaymentUpdateRequest.builder()
                        .billId(found.getBillId())
                        .bbpsTxnRef("REF-1")
                        .amountPaid(new BigDecimal("120.00"))
                        .paymentMode("UPI")
                        .build()
        );
        assertTrue(updated);

        BillDetails afterUpdate = provider.findLatestUnpaidBill(criteria);
        assertNotNull(afterUpdate);
        assertEquals(1L, afterUpdate.getBillId());
    }
}
