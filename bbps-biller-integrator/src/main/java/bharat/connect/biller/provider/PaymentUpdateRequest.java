package bharat.connect.biller.provider;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PaymentUpdateRequest {
    private final Long billId;
    private final String bbpsTxnRef;
    private final BigDecimal amountPaid;
    private final String paymentMode;
}
