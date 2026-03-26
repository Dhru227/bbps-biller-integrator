package bharat.connect.biller.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BillDetails {

    private Long billId;
    private String customerParamName;
    private String customerParamType;
    private String customerParamValue;
    private BigDecimal billAmount;
    private LocalDate billDate;
    private LocalDate dueDate;
    private String billNumber;
    private String billPeriod;
    private String billStatus;
    private String additionalInfo; // Represented as String for JSONB
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
