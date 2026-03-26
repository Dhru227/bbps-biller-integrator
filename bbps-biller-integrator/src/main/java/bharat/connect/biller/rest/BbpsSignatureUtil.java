package bharat.connect.biller.rest;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class BbpsSignatureUtil {
    public static boolean isXmlDigitalSignatureValid(Document doc, PublicKey publicKey) throws Exception {
        boolean validFlag = false;
        NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS,
                "Signature");
        if (nl.getLength() == 0) {
            return false;
        }
        DOMValidateContext valContext = new DOMValidateContext(publicKey,
                nl.item(0));
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
        XMLSignature signature = fac.unmarshalXMLSignature(valContext);
        validFlag = signature.validate(valContext);
        return validFlag;
    }

    public static Document getXmlDocument(String xml) throws SAXException, IOException, ParserConfigurationException {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        return doc;
    }

    public static void generateXMLDigitalSignature(Document doc,
                                                   PrivateKey privateKey, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, MarshalException, XMLSignatureException {
        XMLSignatureFactory xmlSigFactory = XMLSignatureFactory
                .getInstance("DOM");

        DOMSignContext domSignCtx = new DOMSignContext(privateKey,
                doc.getDocumentElement());
        Reference ref = null;
        SignedInfo signedInfo = null;
        SignatureMethod signatureMethod = null;
        if(System.getProperty("signMethod") != null
                && System.getProperty("signMethod").equalsIgnoreCase("DSA")){
            //DSA 1024 bit
            signatureMethod = xmlSigFactory.newSignatureMethod(SignatureMethod.DSA_SHA1, null);
        } else {
            //RSA 2048 bit
            signatureMethod = xmlSigFactory.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", (SignatureMethodParameterSpec)null);
        }

        ref = xmlSigFactory.newReference("", xmlSigFactory.newDigestMethod(
                DigestMethod.SHA256, null), Collections
                .singletonList(xmlSigFactory.newTransform(Transform.ENVELOPED,
                        (TransformParameterSpec) null)), null, null);
        signedInfo = xmlSigFactory.newSignedInfo(xmlSigFactory
                        .newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                                (C14NMethodParameterSpec) null), signatureMethod,
                Collections.singletonList(ref));
        // Pass the Public Key File Path
        KeyInfo keyInfo = getKeyInfo(xmlSigFactory, publicKey);
        // Create a new XML Signature
        XMLSignature xmlSignature = xmlSigFactory.newXMLSignature(signedInfo,
                keyInfo);
        // Sign the document
        xmlSignature.sign(domSignCtx);
    }

    private static KeyInfo getKeyInfo(XMLSignatureFactory xmlSigFactory, PublicKey publicKey) {
        KeyInfo keyInfo = null;
        KeyValue keyValue = null;
        KeyInfoFactory keyInfoFact = xmlSigFactory.getKeyInfoFactory();

        try {
            keyValue = keyInfoFact.newKeyValue(publicKey);
        } catch (KeyException ex) {
            ex.printStackTrace();
        }
        keyInfo = keyInfoFact.newKeyInfo(Collections.singletonList(keyValue));
        return keyInfo;
    }

    public static String convert(Document doc) throws TransformerException {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer trans = null;
        trans = transFactory.newTransformer();
        StringWriter sw = new StringWriter();
        StreamResult streamRes = new StreamResult(sw);
        trans.transform(new DOMSource(doc), streamRes);
        sw.flush();
        return sw.toString();
    }
}
