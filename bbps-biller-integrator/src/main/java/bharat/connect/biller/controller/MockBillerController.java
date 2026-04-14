package bharat.connect.biller.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/mock-biller/{billerId}")
public class MockBillerController {

    @PostMapping("/fetch")
    public ResponseEntity<Map<String, Object>> mockFetch(
            @PathVariable String billerId,
            @RequestBody Map<String, Object> requestParams) {
        
        // For the POC, we blindly accept whatever customer params are passed 
        // and return a dynamically generated dummy bill.
        Map<String, Object> mockBillResponse = Map.of(
                "status", "SUCCESS",
                "billerId", billerId,
                "billAmount", 1250.50,
                "dueDate", "2026-05-15",
                "billNumber", "MB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "customerName", "POC Test User"
        );
        
        System.out.println("=== Mock Biller Stub: Served FETCH for " + billerId + " ===");
        return ResponseEntity.ok(mockBillResponse);
    }

    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> mockPayment(
            @PathVariable String billerId,
            @RequestBody Map<String, Object> requestParams) {
        
        Map<String, Object> mockReceipt = Map.of(
                "status", "PAID",
                "billerId", billerId,
                "txnReference", "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "message", "Payment recorded successfully at Mock Stub"
        );
        
        System.out.println("=== Mock Biller Stub: Served PAYMENT for " + billerId + " ===");
        return ResponseEntity.ok(mockReceipt);
    }
}
