package bharat.connect.biller.rest;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class OuClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
    @Override
    protected void prepareConnection(HttpURLConnection connection,
                                     String httpMethod) throws IOException {
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection)
                    .setSSLSocketFactory(initSSLContext().getSocketFactory());

            if (System.getProperty("bypass.hostname.check") == null
                    || System.getProperty("bypass.hostname.check").equalsIgnoreCase("true")){
                ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        // TODO Auto-generated method stub
                        return true;
                    }
                });

            }
        }
        super.prepareConnection(connection, httpMethod);
    }

    private SSLContext initSSLContext() {
        try {
            //System.setProperty("https.protocols", "TLSv1");
            //final SSLContext ctx = SSLContext.getInstance("TLSv1");

            System.setProperty("https.protocols",  System.getProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2"));
            final SSLContext ctx = SSLContext.getInstance("TLS");

            final BbpsX509TrustManager trustManager = new BbpsX509TrustManager();
            ctx.init(null, new TrustManager[] { trustManager }, null);
            return ctx;
        } catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
