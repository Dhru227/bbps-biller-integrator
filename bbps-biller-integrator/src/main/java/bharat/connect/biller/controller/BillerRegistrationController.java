package bharat.connect.biller.controller;

import bharat.connect.biller.dto.BillerRegistrationRequest;
import bharat.connect.biller.dto.BillerRegistrationResponse;
import bharat.connect.biller.service.BillerRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/biller")
public class BillerRegistrationController {

    @Autowired
    private BillerRegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<BillerRegistrationResponse> registerBiller(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey,
            @RequestBody BillerRegistrationRequest request) {

        if (!"POC-SECRET-KEY-123".equals(apiKey)) {
            return ResponseEntity.status(401).build();
        }

        if (request.getBillerRefId() == null || request.getEntityName() == null) {
            return ResponseEntity.badRequest().build();
        }

        BillerRegistrationResponse response = registrationService.register(request);
        return ResponseEntity.ok(response);
    }
}
