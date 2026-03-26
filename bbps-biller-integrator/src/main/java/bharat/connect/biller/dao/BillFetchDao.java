package bharat.connect.biller.dao;

import bharat.connect.biller.model.BillDetails;
import org.bbps.schema.CustomerParamsType;

import java.util.Optional;

public interface BillFetchDao {
    BillDetails findLatestUnpaidByParam(String paramName, String paramValue);

    BillDetails findLatestUnpaidBillByCustomerParams(CustomerParamsType customerParamsType);
}
