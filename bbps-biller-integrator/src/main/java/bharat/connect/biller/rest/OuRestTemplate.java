package bharat.connect.biller.rest;

import org.bbps.schema.Ack;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OuRestTemplate {
    private final RestTemplate restTemplate;

    // private static int MAX_RETRY_COUNT = 3;
    private static int MAX_RETRY_COUNT = 0;

    private OuRestTemplate() {
        restTemplate = new RestTemplate(clientHttpRequestFactory());
        ClientHttpRequestInterceptor ri = new OuRestTemplateInterceptor();
        List<ClientHttpRequestInterceptor> ris = new ArrayList<>(1);
        ris.add(ri);
        restTemplate.setInterceptors(ris);
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        OuClientHttpRequestFactory factory = new OuClientHttpRequestFactory();
        factory.setReadTimeout(100000);
        factory.setConnectTimeout(1000);
        factory.setBufferRequestBody(false);


        return factory;
    }

    /*
     * @return the object of RestTemplate which contains the Interceptors and
     * RequestFactory so that the request and response XML can be validated
     * automatically
     */
    public static OuRestTemplate createInstance() {
        // return the RestTemplate object
        return new OuRestTemplate();
    }

    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables) {
        int i = 0;
        while (true) {
            try {
                return restTemplate.postForEntity(url, request, responseType, uriVariables);
            } catch (ResourceAccessException e) {
                if (i >= MAX_RETRY_COUNT) {
                    throw e;
                }
            }
            i++;
        }

    }

    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType) {
        int i = 0;
        while (true) {
            try {
                return restTemplate.postForEntity(url, request, responseType);
            } catch (ResourceAccessException e) {
                if (i >= MAX_RETRY_COUNT) {
                    throw e;
                }
            } catch (Exception e) {
                System.err.println("OuRestTemplate.postForEntity(String, Object, Class<T>) :" + url + " " + request);
                throw e;
            }
            i++;
        }
    }

    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, MediaType contentType, MediaType acceptHeader) {
        int i = 0;
        while (true) {
            try {
                MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
                headers.add(HttpHeaders.CONTENT_TYPE, contentType.toString());
                headers.add(HttpHeaders.ACCEPT, acceptHeader.toString());
                return restTemplate.postForEntity(url, new HttpEntity<>(request, headers), responseType);
            } catch (ResourceAccessException e) {
                if (i >= MAX_RETRY_COUNT) {
                    throw e;
                }
            }
            i++;
        }
    }

    public <T> ResponseEntity<T> putForEntity(String url, Object request, Class<T> responseType, MediaType contentType, MediaType acceptHeader) {
        int i = 0;
        while (true) {
            try {
                MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
                headers.add(HttpHeaders.CONTENT_TYPE, contentType.toString());
                headers.add(HttpHeaders.ACCEPT, acceptHeader.toString());
                return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(request, headers), responseType);
            } catch (ResourceAccessException e) {
                if (i >= MAX_RETRY_COUNT) {
                    throw e;
                }
            }
            i++;
        }
    }

    public <T> T postForObject(String url, Object request, Class<T> responseType) {
        int i = 0;
        while (true) {
            try {
                return restTemplate.postForObject(url, request, responseType);
            } catch (ResourceAccessException e) {
                if (i >= MAX_RETRY_COUNT) {
                    throw e;
                }
            }
            i++;
        }

    }

    public <T> ResponseEntity<Ack> postForMultipart(String url, File request, Class<Ack> responseType, String multipartFormDataValue, MediaType acceptHeader, String ouId) {
        int i = 0;
        while (true) {
            try {
                MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
                headers.add(HttpHeaders.CONTENT_TYPE, multipartFormDataValue);
                headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

                MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
                form.add("file", request);
                form.add("origInstId", ouId);

                return restTemplate.postForEntity(url, new HttpEntity<>(form, headers), responseType);
            } catch (ResourceAccessException e) {
                if (i >= MAX_RETRY_COUNT) {
                    throw e;
                }
            }
            i++;
        }
    }
}
