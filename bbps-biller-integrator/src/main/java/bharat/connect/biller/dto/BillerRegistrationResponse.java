package bharat.connect.biller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillerRegistrationResponse {
    private String status;
    private String billerId;
    private String mockFetchUrl;
    private String mockPaymentUrl;
}
