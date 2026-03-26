package bharat.connect.biller.service.impl;

import bharat.connect.biller.common.CommonUtils;
import bharat.connect.biller.rest.OuRestTemplate;
import bharat.connect.biller.service.HeartbeatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.bbps.schema.HeadType;
import org.bbps.schema.ReqDiagnostic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class HeartbeatServiceImpl implements HeartbeatService {

    private OuRestTemplate restTemplate = OuRestTemplate.createInstance();

    @Value("${diagnosticReqFormat:application/xml}")
    private String contentType;

    @Value("${julianDt.refId:YES}")
    private String julianDtRefId;

    @Value("${julianDateDelay:0}")
    private int julianDateDelay;

    @Value("${hbt.frequency:1}")
    private String hbtFreq;

    @Value("${cu.domain}")
    private String cuDomain;

    @Value("${ou.id}")
    private String ouId;

    public static final String CU_DIAGNOSTIC_REQUEST_URL = "/bbps/ReqHbt/1.0/urn:referenceId:";

    @Override
    public String sendHeartbeat() {
        ResponseEntity<String> resp = null;
        ReqDiagnostic reqHbt = new ReqDiagnostic();
        ReqDiagnostic reqHbtJson = new ReqDiagnostic();
        String headRefId = "";
        String mediaType = contentType.equalsIgnoreCase("JSON") ? MediaType.APPLICATION_JSON_VALUE : MediaType.APPLICATION_XML_VALUE;
        if (julianDtRefId.equalsIgnoreCase("YES"))
            headRefId = generateJulianDtId();
        else
            headRefId = StringUtils.capitalize(RandomStringUtils.randomAlphanumeric(35));

        System.out.println("=====hbt=====" + ouId);
        String cuDiagnosticUrlUrl = cuDomain + CU_DIAGNOSTIC_REQUEST_URL + headRefId;
        System.out.println("=====cuDiagnosticUrlUrl=====>>" + cuDiagnosticUrlUrl);
        if (mediaType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {

            HeadType head = new HeadType();
            head.setTs(CommonUtils.getFormattedCurrentTimestamp());
            head.setOrigInst(ouId);
            head.setRefId(headRefId);
            head.setVer("1.0");
            reqHbtJson.setHead(head);
            ObjectMapper mapper = new ObjectMapper();
            try {
                System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqHbtJson));

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            resp = restTemplate.postForEntity(cuDiagnosticUrlUrl, reqHbtJson, String.class,
                    MediaType.valueOf(mediaType), MediaType.valueOf(mediaType));

        } else {
            try {
                org.bbps.schema.HeadType head = new org.bbps.schema.HeadType();
                head.setTs(CommonUtils.getFormattedCurrentTimestamp());
                head.setOrigInst(ouId);
                head.setRefId(headRefId);
                head.setVer("1.0");
                reqHbt.setHead(head);
                JAXBContext jaxbContext = JAXBContext.newInstance(ReqDiagnostic.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(reqHbt, System.out);
            } catch (Exception e) {
                e.printStackTrace();
            }
            resp = restTemplate.postForEntity(cuDiagnosticUrlUrl, reqHbt, String.class,
                    MediaType.valueOf(mediaType), MediaType.valueOf(mediaType));

        }
        try {
            if (MediaType.APPLICATION_JSON_VALUE.equals(mediaType)) {
                System.out.println("ApplicationContext.HeartBeatGenerator.run() response Json:" + new ObjectMapper().readTree(resp.getBody()).toPrettyString());
            } else {
                System.out.println("ApplicationContext.HeartBeatGenerator.run() response:" + resp.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp.getBody();
    }

    private String generateJulianDtId() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        //Random random = new Random();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 27; i++) {
            char c = chars[random.nextInt(27)];
            sb.append(c);
        }
        if (julianDateDelay > 0)
            sb.append(LocalDate.now().plusDays(julianDateDelay).format(DateTimeFormatter.ofPattern("yDDD")).substring(3));
        else if (julianDateDelay < 0)
            sb.append(LocalDate.now().minusDays(Math.abs(julianDateDelay)).format(DateTimeFormatter.ofPattern("yDDD")).substring(3));
        else if (julianDateDelay == 0)
            sb.append(LocalDate.now().format(DateTimeFormatter.ofPattern("YDDD")).substring(3));

        sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm")));
        String output = sb.toString();
        return output.toUpperCase();
    }
}
