package bharat.connect.biller.provider;

import bharat.connect.biller.model.BillDetails;

import java.util.List;

public interface BillingProvider {
    BillDetails findLatestUnpaidBill(List<CustomerParamCriterion> criteria);

    boolean markBillPaidAndRecordTransaction(List<CustomerParamCriterion> criteria, PaymentUpdateRequest paymentUpdateRequest);
}
