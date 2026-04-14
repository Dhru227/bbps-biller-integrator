package bharat.connect.biller.service;

import bharat.connect.biller.dao.BillerRegistrationDao;
import bharat.connect.biller.dto.BillerRegistrationRequest;
import bharat.connect.biller.dto.BillerRegistrationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BillerRegistrationService {

    @Autowired
    private BillerRegistrationDao registrationDao;

    @Value("${integrator.baseUrl:http://localhost:8111}")
    private String baseUrl;

    @Autowired
    private bharat.connect.biller.cache.BillerRoutingCache routingCache;

    @Transactional
    public BillerRegistrationResponse register(BillerRegistrationRequest request) {
        String generatedBillerId = "BLR-POC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String mockFetchUrl = baseUrl + "/mock-biller/" + generatedBillerId + "/fetch";
        String mockPaymentUrl = baseUrl + "/mock-biller/" + generatedBillerId + "/payment";

        try {
            registrationDao.saveRegisteredBiller(generatedBillerId, request, mockFetchUrl, mockPaymentUrl);

            // --- NEW LINE: Add to in-memory routing cache instantly ---
            routingCache.addRoute(generatedBillerId, mockFetchUrl);
        } catch (DuplicateKeyException e) {
            return BillerRegistrationResponse.builder()
                    .status("ALREADY_REGISTERED")
                    .billerId("DUPLICATE_REF:" + request.getBillerRefId())
                    .build();
        }

        return BillerRegistrationResponse.builder()
                .status("REGISTERED")
                .billerId(generatedBillerId)
                .mockFetchUrl(mockFetchUrl)
                .mockPaymentUrl(mockPaymentUrl)
                .build();
    }
}
