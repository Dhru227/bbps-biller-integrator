package bharat.connect.biller.provider;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class CustomerParamCriterion {
    private final String name;
    private final String value;
}
