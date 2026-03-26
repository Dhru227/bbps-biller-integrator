package bharat.connect.biller.service.impl;

import bharat.connect.biller.common.CommonUtils;
import bharat.connect.biller.dao.BillFetchDao;
import bharat.connect.biller.dao.BillPaymentDao;
import bharat.connect.biller.model.BillDetails;
import bharat.connect.biller.rest.OuRestTemplate;
import bharat.connect.biller.service.BillPaymentService;
import org.bbps.schema.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.math.BigDecimal;

@Service
public class BillPaymentServiceImpl implements BillPaymentService {

    @Autowired
    private BillFetchDao billFetchDao;

    @Autowired
    private BillPaymentDao billPaymentDao;

    @Value("${ou.id}")
    private String ouId;

    @Value("${bbps.ip:${cu.domain}}")
    private String bbpsIp;

    @Value("${bbps.billpaymentresponse.url:${cu.billpaymentresponse.url}}")
    private String billPaymentResponseUrl;

    private final OuRestTemplate ouRestTemplate = OuRestTemplate.createInstance();

    private static final JAXBContext jaxbContext;

    static {
        JAXBContext jaxbContext2;
        try {
            jaxbContext2 = JAXBContext.newInstance(Ack.class, BillPaymentResponse.class);
        } catch (JAXBException e) {
            e.printStackTrace();
            jaxbContext2 = null;
        }
        jaxbContext = jaxbContext2;
    }

    @Async
    @Override
    public void processBillPaymentAsync(BillPaymentRequest paymentRequest, String referenceId) {
        System.out.println("Processing Bill Payment");
        if (paymentRequest == null || paymentRequest.getBillDetails() == null || paymentRequest.getBillDetails().getCustomerParams() == null) {
            System.out.println("BillPaymentRequest or CustomerParams is null, skipping.");
            return;
        }

        CustomerParamsType customerParams = paymentRequest.getBillDetails().getCustomerParams();

        // Read the unpaid bill first to get bill details for the response
        BillDetails bd = billFetchDao.findLatestUnpaidBillByCustomerParams(customerParams);
        if (bd == null) {
            System.out.println("No unpaid bill found for payment");
            return;
        }

        String ts = CommonUtils.getFormattedCurrentTimestamp();
        String approvalRefNum = "BP" + (System.currentTimeMillis() % 1_000_000_000L);

        String bbpsTxnRef = paymentRequest.getHead().getRefId();
        BigDecimal amountPaid = bd.getBillAmount();
        String paymentMode = null;
        if (paymentRequest.getPaymentMethod() != null) {
            paymentMode = paymentRequest.getPaymentMethod().getPaymentMode();
        }

        try {
            // Single @Transactional call: marks bill_details as PAID + inserts payment_transactions.
            // Both succeed or both rollback.
            boolean success = billPaymentDao.markBillPaidAndRecordTransaction(
                    customerParams, bd.getBillId(), bbpsTxnRef, amountPaid, paymentMode);

            if (!success) {
                System.out.println("DB update failed — bill_status not updated or payment_transactions insert failed. Skipping response.");
                return;
            }

            System.out.println("DB success: bill_id=" + bd.getBillId()
                    + " marked PAID, payment_transactions row created (txnRef=" + bbpsTxnRef + ")");

            // Both DB operations succeeded — build and send BillPaymentResponse
            BillPaymentResponse resp = new BillPaymentResponse();

            resp.setHead(paymentRequest.getHead());
            resp.setTxn(paymentRequest.getTxn());
            resp.setBillDetails(paymentRequest.getBillDetails());

            resp.getHead().setRefId(paymentRequest.getHead().getRefId());
            resp.getHead().setTs(ts);
            resp.getHead().setOrigInst(ouId);

            resp.getTxn().setTs(paymentRequest.getTxn().getTs());
            resp.getTxn().setMsgId(paymentRequest.getTxn().getMsgId());
            resp.getTxn().setTxnReferenceId(paymentRequest.getTxn().getTxnReferenceId());
            resp.getTxn().setRiskScores(null);

            ReasonType reason = new ReasonType();
            reason.setResponseCode("000");
            reason.setApprovalRefNum(approvalRefNum);
            reason.setResponseReason("Successful");
            resp.setReason(reason);

            if (paymentRequest.getBillerResponses() != null && !paymentRequest.getBillerResponses().isEmpty()) {
                resp.getBillerResponses().addAll(paymentRequest.getBillerResponses());
            } else {
                BillerResponseType br = new BillerResponseType();
                br.setAmount(String.valueOf(bd.getBillAmount()));
                br.setBillDate(String.valueOf(bd.getBillDate()));
                br.setDueDate(String.valueOf(bd.getDueDate()));
                resp.getBillerResponses().add(br);
            }

            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            marshaller.marshal(resp, sw);

            System.out.println("========================= BillPaymentResponse SENT ================================");
            System.out.println(sw);

            String bbpsBillPaymentResponseUrl = bbpsIp + billPaymentResponseUrl + resp.getHead().getRefId();
            ResponseEntity<Ack> ackResponse = ouRestTemplate.postForEntity(
                    bbpsBillPaymentResponseUrl,
                    resp,
                    Ack.class,
                    MediaType.APPLICATION_XML,
                    MediaType.APPLICATION_XML
            );

            System.out.println("==================== Received Ack against Bill Payment Response ===============");
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
        } catch (Exception e) {
            System.out.println("ERROR in processBillPaymentAsync for txnRef=" + bbpsTxnRef + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
