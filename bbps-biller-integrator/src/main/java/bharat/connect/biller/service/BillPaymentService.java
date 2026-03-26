package bharat.connect.biller.service;

import org.bbps.schema.BillPaymentRequest;

public interface BillPaymentService {
    void processBillPaymentAsync(BillPaymentRequest paymentRequest, String referenceId);
}

