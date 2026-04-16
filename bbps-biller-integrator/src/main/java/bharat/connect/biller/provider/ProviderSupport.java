package bharat.connect.biller.provider;

import bharat.connect.biller.model.BillDetails;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public final class ProviderSupport {
    private ProviderSupport() {
    }

    public static boolean matches(List<CustomerParamCriterion> criteria, BillDetails details) {
        if (criteria == null || criteria.isEmpty() || details == null) {
            return false;
        }
        for (CustomerParamCriterion criterion : criteria) {
            if (criterion == null) {
                continue;
            }
            if (criterion.getName().equals(details.getCustomerParamName())
                    && criterion.getValue().equals(details.getCustomerParamValue())) {
                return true;
            }
        }
        return false;
    }

    public static Comparator<BillDetails> latestUnpaidOrder() {
        Comparator<BillDetails> dueDateComparator = Comparator.comparing(
                BillDetails::getDueDate,
                Comparator.nullsLast(Comparator.naturalOrder())
        );
        Comparator<BillDetails> billDateComparator = Comparator.comparing(
                BillDetails::getBillDate,
                Comparator.nullsLast(Comparator.reverseOrder())
        );
        Comparator<BillDetails> billIdComparator = Comparator.comparing(
                BillDetails::getBillId,
                Comparator.nullsLast(Comparator.reverseOrder())
        );
        return dueDateComparator.thenComparing(billDateComparator).thenComparing(billIdComparator);
    }

    public static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value.trim());
    }
}
