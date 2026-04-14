package bharat.connect.biller.dto;

import lombok.Data;
import java.util.Map;

@Data
public class BillerRegistrationRequest {
    private String billerRefId;
    private String entityName;
    private String billCategory;
    private Map<String, String> customerParams;
}
