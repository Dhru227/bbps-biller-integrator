package bharat.connect.biller.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.rmi.MarshalException;
import java.security.*;
import java.security.cert.CertificateException;

public class OuRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private static PrivateKey privateKey = null;
    private static PublicKey publicKey = null;
    private static String certPath = null;
    static {
        try {
            System.out.println("=================OuRestTemplateInterceptor=================================");
            KeyStore keystore = KeyStore.getInstance("PKCS12");

            /* Information for certificate to be generated */
            //			String password = "rssoftware";
            String password = System.getProperty("keystorepwd", "npciupi");
            String alias = "1";
			/*			String password = "123456";
			String alias = "bbps";*/
            //			certPath = System.getProperty("cert.path") == null ? ""
            //					: System.getProperty("cert.path") + "/ousigner.p12";

            //certPath = System.getenv("BBPS_HOME")+"/config/certificate/signer/signer.p12";
            certPath = System.getenv("BBPS_HOME")+"/config/certificate/OU/ousigner.p12";
            if (!certPath.equalsIgnoreCase("")) {
                InputStream is = new FileInputStream(new File(certPath));
                InputStream caInput = new BufferedInputStream(is);

                /* getting the key */
                keystore.load(caInput, password.toCharArray());
                PrivateKey key = (PrivateKey) keystore.getKey(alias,
                        password.toCharArray());

                privateKey = key;

                java.security.cert.Certificate cert = keystore
                        .getCertificate(alias);
                publicKey = cert.getPublicKey();
            }
        } catch (UnrecoverableKeyException | KeyStoreException
                 | NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        try {
            if(certPath != null) {
                if (!CollectionUtils.isEmpty(request.getHeaders().get(HttpHeaders.CONTENT_TYPE))
                        && MediaType.APPLICATION_JSON_VALUE.equals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0))) {
                    if(request.getURI().getPath().contains("/agent/1.0/") ) {
                        body = signJson(request, body, 1);
                    }else if(request.getURI().getPath().contains("/CustAuthRequest/1.0/referenceId:") ) {
                        body = signJson(request, body, 2);

                    }else {
                        body = signJson(request, body , 3);
                    }
                    int contentLength = body.length;
                    if (request.getHeaders().containsKey("Content-Length")) {
                        request.getHeaders().remove("Content-Length");
                    }
                    request.getHeaders().add("Content-Length",
                            String.valueOf(contentLength));
                } else if (!CollectionUtils.isEmpty(request.getHeaders().get(HttpHeaders.CONTENT_TYPE))
                        && MediaType.APPLICATION_XML_VALUE.equals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0))){
                    if(body[0] == '<') {
                        body = signXML(request, body);
                    }
                    int contentLength = body.length;
                    if (request.getHeaders().containsKey("Content-Length")) {
                        request.getHeaders().remove("Content-Length");
                    }
                    request.getHeaders().add("Content-Length",
                            String.valueOf(contentLength));
                }
            }


            ClientHttpResponse response = execution.execute(request, body);
            return response;

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | SAXException |
                 ParserConfigurationException | MarshalException | XMLSignatureException | TransformerException |
                 javax.xml.crypto.MarshalException e) {
            throw new IOException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] signXML(HttpRequest request, byte[] body)
            throws IOException, SAXException, ParserConfigurationException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            MarshalException, XMLSignatureException, TransformerException, javax.xml.crypto.MarshalException {
        Document doc = BbpsSignatureUtil.getXmlDocument(new String(body));
        BbpsSignatureUtil.generateXMLDigitalSignature(doc, privateKey,
                publicKey);

        return BbpsSignatureUtil.convert(doc).getBytes();
    }

    private byte[] signJson(HttpRequest request, byte[] body, int num)
            throws JOSEException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readValue(body, JsonNode.class);
        String key = null;
        if (isNotEmpty(jsonNode.get("head")) && isNotEmpty(jsonNode.get("head").get("origInst"))){
            key =  jsonNode.get("head").get("origInst").asText();
        } else if (isNotEmpty(jsonNode.get("cou_id"))) {
            key = jsonNode.get("cou_id").asText();
        } else if (isNotEmpty(jsonNode.get("origInstId"))) {
            key = jsonNode.get("origInstId").asText();
        } else if (isNotEmpty(jsonNode.get("billerId"))) {
            key = jsonNode.get("billerId").asText();
        } else if (isNotEmpty(jsonNode.get("blr_linked_ou_default"))) {
            key = jsonNode.get("blr_linked_ou_default").asText();
        }

        if(key == null){
            throw new IllegalArgumentException("For signing the payload ouId is mandatory like origInst,blr_linked_ou_default,cou_id");
        }
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key).build();
        JWSObject jwsObject = new JWSObject(jwsHeader, new Payload(new String(body)));
        jwsObject.sign(new RSASSASigner(privateKey));

        return jwsObject.serialize().getBytes();

    }
    private boolean isNotEmpty(JsonNode id) {
        return id!=null && !id.isNull() && !id.toString().isEmpty();
    }
}
