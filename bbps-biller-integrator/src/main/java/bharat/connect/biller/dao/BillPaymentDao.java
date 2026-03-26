package bharat.connect.biller.dao;

import org.bbps.schema.CustomerParamsType;

import java.math.BigDecimal;

public interface BillPaymentDao {

    /**
     * Atomically marks the latest UNPAID bill (matched by customer params) as PAID
     * and inserts a row into payment_transactions.
     *
     * @param billId the bill_id obtained from the prior findLatestUnpaidBillByCustomerParams call
     * @return true if both UPDATE and INSERT succeeded, false otherwise.
     */
    boolean markBillPaidAndRecordTransaction(CustomerParamsType customerParams,
                                             long billId,
                                             String bbpsTxnRef,
                                             BigDecimal amountPaid,
                                             String paymentMode);
}
