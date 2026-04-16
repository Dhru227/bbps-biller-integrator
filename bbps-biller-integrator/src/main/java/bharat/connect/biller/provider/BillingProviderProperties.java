package bharat.connect.biller.provider;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "billing.provider")
public class BillingProviderProperties {
    private String type = "postgres";
    private String dateFormat = "yyyy-MM-dd";

    private Csv csv = new Csv();
    private Excel excel = new Excel();
    private Postgres postgres = new Postgres();

    @Getter
    @Setter
    public static class Csv {
        private String path;
        private String paymentLogPath;
        private String delimiter = ",";
        private Columns columns = new Columns();
    }

    @Getter
    @Setter
    public static class Excel {
        private String path;
        private String sheetName = "Sheet1";
        private String paymentLogPath;
        private Columns columns = new Columns();
    }

    @Getter
    @Setter
    public static class Columns {
        private String billId = "bill_id";
        private String customerParamName = "customer_param_name";
        private String customerParamValue = "customer_param_value";
        private String customerParamType = "customer_param_type";
        private String billAmount = "bill_amount";
        private String billDate = "bill_date";
        private String dueDate = "due_date";
        private String billNumber = "bill_number";
        private String billPeriod = "bill_period";
        private String billStatus = "bill_status";
    }

    @Getter
    @Setter
    public static class Postgres {
        private Tables tables = new Tables();
        private BillColumns billColumns = new BillColumns();
        private PaymentColumns paymentColumns = new PaymentColumns();
    }

    @Getter
    @Setter
    public static class Tables {
        private String billDetails = "bill_details";
        private String paymentTransactions = "payment_transactions";
    }

    @Getter
    @Setter
    public static class BillColumns {
        private String billId = "bill_id";
        private String customerParamName = "customer_param_name";
        private String customerParamType = "customer_param_type";
        private String customerParamValue = "customer_param_value";
        private String billAmount = "bill_amount";
        private String billDate = "bill_date";
        private String dueDate = "due_date";
        private String billNumber = "bill_number";
        private String billPeriod = "bill_period";
        private String billStatus = "bill_status";
        private String additionalInfo = "additional_info";
        private String createdAt = "created_at";
        private String updatedAt = "updated_at";
    }

    @Getter
    @Setter
    public static class PaymentColumns {
        private String billId = "bill_id";
        private String bbpsTxnRef = "bbps_txn_ref";
        private String amountPaid = "amount_paid";
        private String paymentMode = "payment_mode";
    }
}
