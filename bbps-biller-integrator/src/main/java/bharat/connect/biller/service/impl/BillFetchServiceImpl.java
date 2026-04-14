package bharat.connect.biller.service.impl;

import bharat.connect.biller.common.CommonUtils;
import bharat.connect.biller.dao.BillFetchDao;
import bharat.connect.biller.model.BillDetails;
import bharat.connect.biller.rest.OuRestTemplate;
import bharat.connect.biller.service.BillFetchService;
import org.bbps.schema.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import bharat.connect.biller.cache.BillerRoutingCache;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@Service
public class BillFetchServiceImpl implements BillFetchService {

    @Autowired
    private BillFetchDao billFetchDao;

    @Autowired
    private BillerRoutingCache routingCache;

    private final RestTemplate jsonRestTemplate = new RestTemplate();

    @Value("${ou.id}")
    private String ouId;

    @Value("${bbps.ip:${cu.domain}}")
    private String bbpsIp;

    @Value("${bbps.billfetchresponse.url:${cu.billfetchresponse.url}}")
    private String billFetchResponseUrl;

    private final OuRestTemplate ouRestTemplate = OuRestTemplate.createInstance();

    private static final JAXBContext jaxbContext;

    static {
        JAXBContext jaxbContext2 = null;
        try {
            jaxbContext2 = JAXBContext.newInstance(Ack.class, BillFetchResponse.class);
        } catch (JAXBException e) {
            e.printStackTrace();
            jaxbContext2 = null;
        }
        jaxbContext = jaxbContext2;
    }

    @Async
    @Override
    public void processBillFetchAsync(BillFetchRequest fetchRequest, String referenceId) {
        System.out.println("Processing Bill Fetch");
        if (fetchRequest == null || fetchRequest.getBillDetails() == null || fetchRequest.getBillDetails().getCustomerParams() == null) return;
        
        String billerId = fetchRequest.getHead() != null ? fetchRequest.getHead().getOrigInst() : null;
        String mockFetchUrl = billerId != null ? routingCache.getFetchUrl(billerId) : null;
        
        BillDetails bd = new BillDetails();

        if (mockFetchUrl != null) {
            System.out.println("=== Routing Fetch dynamically to Mock Stub: " + mockFetchUrl + " ===");
            try {
                // Convert XML Tags to JSON Map for the Mock Biller
                Map<String, String> paramMap = new HashMap<>();
                fetchRequest.getBillDetails().getCustomerParams().getTags()
                        .forEach(tag -> paramMap.put(tag.getName(), tag.getValue()));

                // Fetch dynamically from our Phase 4 controller
                Map<String, Object> mockRes = jsonRestTemplate.postForObject(mockFetchUrl, paramMap, Map.class);
                
                if (mockRes != null && "SUCCESS".equals(mockRes.get("status"))) {
                    bd.setBillAmount(new BigDecimal(mockRes.get("billAmount").toString()));
                    bd.setDueDate(LocalDate.parse(mockRes.get("dueDate").toString()));
                    bd.setBillDate(LocalDate.now()); 
                } else {
                    System.out.println("Mock stub returned failure or null.");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Failed to fetch from Mock Stub: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("=== Biller not in cache. Falling back to DB lookup ===");
            bd = billFetchDao.findLatestUnpaidBillByCustomerParams(fetchRequest.getBillDetails().getCustomerParams());
            if (bd == null) {
                System.out.println("No Bills found in DB");
                return;
            }
        }

        try {
            BillFetchResponse fetchResponse = new BillFetchResponse();
            fetchResponse.setHead(fetchRequest.getHead());
            fetchResponse.setTxn(fetchRequest.getTxn());
            String ts = CommonUtils.getFormattedCurrentTimestamp();
            fetchResponse.getHead().setRefId(fetchRequest.getHead().getRefId());
            fetchResponse.getHead().setTs(ts);
            fetchResponse.getHead().setOrigInst(ouId);
            fetchResponse.getTxn().setTs(fetchRequest.getTxn().getTs());
            fetchResponse.getTxn().setMsgId(fetchRequest.getTxn().getMsgId());
            fetchResponse.getTxn().setTxnReferenceId(fetchRequest.getTxn().getTxnReferenceId());
            BillerResponseType billerResponseType = new BillerResponseType();

            billerResponseType.setAmount(String.valueOf(bd.getBillAmount()));
            billerResponseType.setBillDate(String.valueOf(bd.getBillDate()));
            billerResponseType.setDueDate(String.valueOf(bd.getDueDate()));
//            fetchResponse.getBillerResponses().set(0, billerResponseType);
            fetchResponse.getBillerResponses().add(billerResponseType);
            fetchResponse.setBillDetails(fetchRequest.getBillDetails());
            ReasonType responseReason = new ReasonType();
            responseReason.setResponseCode("000");
            responseReason.setApprovalRefNum("AB123456");
            responseReason.setResponseReason("Successful");
            fetchResponse.setReason(responseReason);
            fetchResponse.getTxn().setRiskScores(null);

            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter sw = new StringWriter();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(fetchResponse, sw);
            System.out.println("========================= BillFetchResponse SENT ================================");
            System.out.println(sw.toString());

            String bbpsBillFetchResponseUrl = bbpsIp + billFetchResponseUrl + fetchResponse.getHead().getRefId();
            ResponseEntity<Ack> ackResponse = ouRestTemplate.postForEntity(
                    bbpsBillFetchResponseUrl,
                    fetchResponse,
                    Ack.class,
                    MediaType.APPLICATION_XML,
                    MediaType.APPLICATION_XML
            );

            System.out.println("==================== Received Ack against Bill Fetch Response ===============");
            System.out.println(ackResponse.getStatusCode());
            if (ackResponse.getBody() != null) {
                JAXBContext ackContext = JAXBContext.newInstance(Ack.class);
                Marshaller ackMarshaller = ackContext.createMarshaller();
                ackMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                StringWriter ackSw = new StringWriter();
                ackMarshaller.marshal(ackResponse.getBody(), ackSw);
                System.out.println(ackSw);
            } else {
                System.out.println("Ack body is null");
            }
        } catch (JAXBException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
