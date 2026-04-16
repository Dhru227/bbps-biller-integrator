package bharat.connect.biller.provider;

import org.bbps.schema.CustomerParamsType;

import java.util.ArrayList;
import java.util.List;

public final class CustomerParamCriteriaMapper {
    private CustomerParamCriteriaMapper() {
    }

    public static List<CustomerParamCriterion> from(CustomerParamsType customerParamsType) {
        List<CustomerParamCriterion> criteria = new ArrayList<>();
        if (customerParamsType == null || customerParamsType.getTags() == null) {
            return criteria;
        }

        for (CustomerParamsType.Tag tag : customerParamsType.getTags()) {
            if (tag == null) {
                continue;
            }
            String name = tag.getName() == null ? null : tag.getName().trim();
            String value = tag.getValue() == null ? null : tag.getValue().trim();
            if (name == null || name.isEmpty() || value == null || value.isEmpty()) {
                continue;
            }
            criteria.add(new CustomerParamCriterion(name, value));
        }
        return criteria;
    }
}
