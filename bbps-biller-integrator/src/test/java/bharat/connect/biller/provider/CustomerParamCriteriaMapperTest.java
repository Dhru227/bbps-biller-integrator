package bharat.connect.biller.provider;

import org.bbps.schema.CustomerParamsType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerParamCriteriaMapperTest {

    @Test
    void shouldNormalizeAndFilterTags() {
        CustomerParamsType customerParamsType = new CustomerParamsType();
        CustomerParamsType.Tag valid = new CustomerParamsType.Tag();
        valid.setName("  mobile  ");
        valid.setValue("  9999999999 ");
        CustomerParamsType.Tag invalid = new CustomerParamsType.Tag();
        invalid.setName(" ");
        invalid.setValue("abc");
        customerParamsType.getTags().add(valid);
        customerParamsType.getTags().add(invalid);

        List<CustomerParamCriterion> criteria = CustomerParamCriteriaMapper.from(customerParamsType);

        assertEquals(1, criteria.size());
        assertEquals("mobile", criteria.get(0).getName());
        assertEquals("9999999999", criteria.get(0).getValue());
    }
}
