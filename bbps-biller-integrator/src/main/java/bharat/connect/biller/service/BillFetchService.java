package bharat.connect.biller.service;

import org.bbps.schema.BillFetchRequest;

public interface BillFetchService {
    void processBillFetchAsync(BillFetchRequest fetchRequest, String referenceId);
}
