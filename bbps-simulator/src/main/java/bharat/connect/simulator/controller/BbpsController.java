package bharat.connect.simulator.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/")
public class BbpsController {

    @Value("${integrator.baseUrl}")
    private String integratorBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BBPS_NS = "http://bbps.org/schema";

    @PostMapping(value = "/bbps/BillFetchRequest/1.0/urn:referenceId:{referenceId}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String receiveBillFetchRequest(@RequestBody String requestXml, @PathVariable("referenceId") String referenceId) {
        System.out.println("=== BBPS Simulator: received BillFetchRequest, forwarding to integrator === refId=" + referenceId);
        System.out.println(requestXml);
        String targetUrl = integratorBaseUrl + "/BillFetchRequest/1.0/urn:referenceId:" + referenceId;
        ResponseEntity<String> resp = forwardXml(targetUrl, requestXml);
        System.out.println("=== BBPS Simulator: Ack from integrator for BillFetchRequest ===");
        System.out.println(resp.getBody());
        return resp.getBody();
    }

    @PostMapping(value = "/bbps/BillPaymentRequest/1.0/urn:referenceId:{referenceId}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String receiveBillPaymentRequest(@RequestBody String requestXml, @PathVariable("referenceId") String referenceId) {
        System.out.println("=== BBPS Simulator: received BillPaymentRequest, forwarding to integrator === refId=" + referenceId);
        System.out.println(requestXml);
        String targetUrl = integratorBaseUrl + "/BillPaymentRequest/1.0/urn:referenceId:" + referenceId;
        ResponseEntity<String> resp = forwardXml(targetUrl, requestXml);
        System.out.println("=== BBPS Simulator: Ack from integrator for BillPaymentRequest ===");
        System.out.println(resp.getBody());
        return resp.getBody();
    }

    @PostMapping(value = "/bbps/BillFetchResponse/1.0/urn:referenceId:{referenceId}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String receiveBillFetchResponse(@RequestBody String responseXml, @PathVariable("referenceId") String referenceId) {
        System.out.println("=== BBPS Simulator: received BillFetchResponse from integrator === refId=" + referenceId);
        System.out.println(responseXml);
        return buildAckXml(referenceId, "FETCH_RESPONSE", "000");
    }

    @PostMapping(value = "/bbps/BillPaymentResponse/1.0/urn:referenceId:{referenceId}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String receiveBillPaymentResponse(@RequestBody String responseXml, @PathVariable("referenceId") String referenceId) {
        System.out.println("=== BBPS Simulator: received BillPaymentResponse from integrator === refId=" + referenceId);
        System.out.println(responseXml);
        return buildAckXml(referenceId, "PAYMENT_RESPONSE", "000");
    }

    private String buildAckXml(String referenceId, String api, String rspCd) {
        String ts = java.time.OffsetDateTime.now().toString();
        // Matches integrator-side `org.bbps.schema.Ack` attributes:
        // api, refId, RspCd, ts
        return "<Ack xmlns=\"" + BBPS_NS + "\" api=\"" + api + "\" refId=\"" + referenceId + "\" RspCd=\"" + rspCd + "\" ts=\"" + ts + "\"/>";
    }

    private ResponseEntity<String> forwardXml(String targetUrl, String xmlBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_XML));
        HttpEntity<String> requestEntity = new HttpEntity<>(xmlBody, headers);
        return restTemplate.postForEntity(targetUrl, requestEntity, String.class);
    }
}

