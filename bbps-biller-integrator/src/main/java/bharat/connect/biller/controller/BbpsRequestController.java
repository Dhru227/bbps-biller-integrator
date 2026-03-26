package bharat.connect.biller.controller;

import bharat.connect.biller.common.API;
import bharat.connect.biller.common.CommonConstants;
import bharat.connect.biller.common.CommonUtils;
import bharat.connect.biller.service.BillPaymentService;
import bharat.connect.biller.service.BillFetchService;
import bharat.connect.biller.service.HeartbeatService;
import org.bbps.schema.Ack;
import org.bbps.schema.BillFetchRequest;
import org.bbps.schema.BillPaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@RestController
@RequestMapping("/")
public class BbpsRequestController {

    @Autowired
    private BillFetchService billFetchService;

    @Autowired
    private BillPaymentService billPaymentService;

    @Autowired
    private HeartbeatService heartbeatService;

    @RequestMapping(value = "/BillFetchRequest/1.0/urn:referenceId:{referenceId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    public Ack billFetchRequest(@RequestBody BillFetchRequest fetchRequest, @PathVariable("referenceId") String referenceId) {
        System.out.println("====================received bill fetch request===============");
        printXml(fetchRequest, BillFetchRequest.class, "BillFetchRequest XML");
        if(fetchRequest != null && fetchRequest.getHead().getRefId()!=null && !fetchRequest.getHead().getRefId().isEmpty()){
            billFetchService.processBillFetchAsync(fetchRequest,fetchRequest.getHead().getRefId());
        }
        fetchRequest.getBillDetails().getCustomerParams().getTags().stream().forEach(a -> System.out.println(a.getName() +" - "+a.getValue()));

        Ack ack = new Ack();
        API action = API.FETCH_REQUEST;
        ack.setRefId(fetchRequest.getHead().getRefId());
        ack.setApi(action.name());
        ack.setTs(CommonUtils.getFormattedCurrentTimestamp());
        ack.setRspCd(CommonConstants.RESP_SUCCESS_MSG);
        printXml(ack, Ack.class, "BillFetchRequest ACK");
        return ack;
    }

    @RequestMapping(value = "/BillPaymentRequest/1.0/urn:referenceId:{referenceId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    public Ack billPaymentRequest(@RequestBody BillPaymentRequest paymentRequest, @PathVariable("referenceId") String referenceId) {
        System.out.println("====================received bill payment request===============");
        printXml(paymentRequest, BillPaymentRequest.class, "BillPaymentRequest XML");

        if (paymentRequest != null && paymentRequest.getHead() != null && paymentRequest.getHead().getRefId() != null && !paymentRequest.getHead().getRefId().isEmpty()) {
            billPaymentService.processBillPaymentAsync(paymentRequest, paymentRequest.getHead().getRefId());
        }

        Ack ack = new Ack();
        API action = API.PAYMENT_REQUEST;
        ack.setRefId(paymentRequest.getHead().getRefId());
        ack.setApi(action.name());
        ack.setTs(CommonUtils.getFormattedCurrentTimestamp());
        ack.setRspCd(CommonConstants.RESP_SUCCESS_MSG);
        printXml(ack, Ack.class, "BillPaymentRequest ACK");
        return ack;
    }

    @RequestMapping(value = "/heartbeat", method = RequestMethod.GET)
    public String billFetchRequest() {
        return heartbeatService.sendHeartbeat();
    }

    private void printXml(Object payload, Class<?> payloadClass, String label) {
        try {
            JAXBContext context = JAXBContext.newInstance(payloadClass);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            marshaller.marshal(payload, sw);
            System.out.println("=============== " + label + " ===============");
            System.out.println(sw);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

}
