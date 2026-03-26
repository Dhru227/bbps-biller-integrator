package bharat.connect.biller.rest;

import javax.net.ssl.X509TrustManager;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class BbpsX509TrustManager implements X509TrustManager {
    static String DEFAULT_TRUST_STORE = "/etc/ssl/certs/java/cacerts";
    static X509TrustManager trustManager;
    private static String certPath = null;

    static {
        System.out.println("=================BbpsX509TrustManager=================================");
        try {
            KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = new FileInputStream(System.getProperty("default.trust.store", "C:/Program Files/Java/jdk1.8.0_77/jre/lib/security/cacerts"));
            try {
                ts.load(in, null);
            } finally {
                in.close();
            }

//			certPath = System.getProperty("cert.path") == null ? ""
//					: System.getProperty("cert.path") + "/haproxy.crt";

            //certPath = "D:/git/bbps_master_merged/config/certificate/ssl/haproxy.crt";
            certPath = System.getenv("BBPS_HOME") + "/config/certificate/ssl/haproxy.crt";
            System.out.println("BbpsX509TrustManager::::" + certPath);
            if (!certPath.equalsIgnoreCase("")) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                InputStream is = new FileInputStream(new File(certPath));
                InputStream caInput = new BufferedInputStream(is);
                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                    ts.setCertificateEntry("" + System.currentTimeMillis(), ca);
                } finally {
                    try {
                        caInput.close();
                    } catch (IOException e) {
                    }
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
                System.out.println("BbpsX509TrustManager.....cert created");
                // initialize a new TMF with the ts we just loaded
                TrustManagerFactory tmf = TrustManagerFactory
                        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(ts);

                TrustManager tms[] = tmf.getTrustManagers();
                for (int i = 0; i < tms.length; i++) {
                    if (tms[i] instanceof X509TrustManager) {
                        trustManager = (X509TrustManager) tms[i];
                        break;
                    }
                }
            }
        } catch (KeyStoreException | NoSuchAlgorithmException
                 | CertificateException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        if (!certPath.equalsIgnoreCase("")) {
            trustManager.checkClientTrusted(chain, authType);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {/*
		if (!certPath.equalsIgnoreCase("")) {
			try {
				trustManager.checkServerTrusted(chain, authType);
			} catch (CertificateException ce) {
				ce.printStackTrace();
				throw ce;
			}
		}
	*/
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        if (!certPath.equalsIgnoreCase("")) {
            return trustManager.getAcceptedIssuers();
        } else
            return null;
    }
}
