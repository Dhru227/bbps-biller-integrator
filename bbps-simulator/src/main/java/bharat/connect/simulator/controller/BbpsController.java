package bharat.connect.simulator.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/")
public class BbpsController {

    @Value("${integrator.baseUrl}")
    private String integratorBaseUrl;

    @Value("${platform.url:http://localhost:8000}")
    private String platformUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BBPS_NS = "http://bbps.org/schema";

    private static final Pattern BILLER_ID_PATTERN = Pattern.compile("(?i)billerId=[\"']([^\"']+)[\"']|<[^:]*:?billerId>([^<]+)</");

    @PostMapping(value = "/bbps/BillFetchRequest/1.0/urn:referenceId:{referenceId}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String receiveBillFetchRequest(@RequestBody String requestXml, @PathVariable("referenceId") String referenceId) {
        System.out.println("=== BBPS Simulator: received BillFetchRequest, forwarding === refId=" + referenceId);
        String baseUrl = resolveIntegratorUrl(requestXml);
        String targetUrl = baseUrl + "/BillFetchRequest/1.0/urn:referenceId:" + referenceId;
        System.out.println("Routing to: " + targetUrl);
        ResponseEntity<String> resp = forwardXml(targetUrl, requestXml);
        System.out.println("=== BBPS Simulator: Ack from integrator for BillFetchRequest ===");
        return resp.getBody();
    }

    @PostMapping(value = "/bbps/BillPaymentRequest/1.0/urn:referenceId:{referenceId}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String receiveBillPaymentRequest(@RequestBody String requestXml, @PathVariable("referenceId") String referenceId) {
        System.out.println("=== BBPS Simulator: received BillPaymentRequest, forwarding === refId=" + referenceId);
        String baseUrl = resolveIntegratorUrl(requestXml);
        String targetUrl = baseUrl + "/BillPaymentRequest/1.0/urn:referenceId:" + referenceId;
        System.out.println("Routing to: " + targetUrl);
        ResponseEntity<String> resp = forwardXml(targetUrl, requestXml);
        System.out.println("=== BBPS Simulator: Ack from integrator for BillPaymentRequest ===");
        return resp.getBody();
    }

    @PostMapping(value = "/bbps/BillFetchResponse/1.0/urn:referenceId:{referenceId}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String receiveBillFetchResponse(@RequestBody String responseXml, @PathVariable("referenceId") String referenceId) {
        System.out.println("=== BBPS Simulator: received BillFetchResponse from integrator === refId=" + referenceId);
        return buildAckXml(referenceId, "FETCH_RESPONSE", "000");
    }

    @PostMapping(value = "/bbps/BillPaymentResponse/1.0/urn:referenceId:{referenceId}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String receiveBillPaymentResponse(@RequestBody String responseXml, @PathVariable("referenceId") String referenceId) {
        System.out.println("=== BBPS Simulator: received BillPaymentResponse from integrator === refId=" + referenceId);
        return buildAckXml(referenceId, "PAYMENT_RESPONSE", "000");
    }

    @GetMapping(value = "/bbps/ReqHbt/1.0/urn:referenceId:{referenceId}", produces = MediaType.APPLICATION_XML_VALUE)
    public String heartbeat(@PathVariable("referenceId") String referenceId) {
        return "<RespHbt><Head refId=\"" + referenceId + "\"/></RespHbt>";
    }

    private String resolveIntegratorUrl(String xml) {
        try {
            Matcher matcher = BILLER_ID_PATTERN.matcher(xml);
            String billerId = null;
            if (matcher.find()) {
                billerId = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            }
            if (billerId != null) {
                Map response = restTemplate.getForObject(platformUrl + "/billers/" + billerId, Map.class);
                if (response != null && response.containsKey("biller_endpoint")) {
                    String endpoint = (String) response.get("biller_endpoint");
                    if (endpoint != null && !endpoint.trim().isEmpty()) {
                        return endpoint;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("=== Routing lookup failed or biller not found. Falling back to default baseUrl. ===");
        }
        return integratorBaseUrl;
    }

    private String buildAckXml(String referenceId, String api, String rspCd) {
        String ts = java.time.OffsetDateTime.now().toString();
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
