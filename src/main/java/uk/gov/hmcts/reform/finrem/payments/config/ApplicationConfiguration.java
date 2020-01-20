package uk.gov.hmcts.reform.finrem.payments.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Configuration
public class ApplicationConfiguration {

    @Autowired
    private HttpConfiguration httpConfiguration;

    @Value("${ssl.verification.enabled}")
    private boolean sslVerificationEnabled;

    @Bean
    public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        if (!sslVerificationEnabled) {
            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, (X509Certificate[] x509Certificates, String value) -> true)
                    .build();

            SSLConnectionSocketFactory sslConnectionSocketFactory =
                    new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

            httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);
        }

        CloseableHttpClient httpClient = httpClientBuilder.build();
        HttpComponentsClientHttpRequestFactory requestFactory = createHttpRequestFactory(httpClient);

        return new RestTemplate(requestFactory);
    }

    private HttpComponentsClientHttpRequestFactory createHttpRequestFactory(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        if (httpConfiguration.getRequestTimeout() >= 0) {
            requestFactory.setConnectionRequestTimeout(httpConfiguration.getRequestTimeout());
        }
        if (httpConfiguration.getTimeout() >= 0) {
            requestFactory.setConnectTimeout(httpConfiguration.getTimeout());
        }
        if (httpConfiguration.getReadTimeout() >= 0) {
            requestFactory.setReadTimeout(httpConfiguration.getReadTimeout());
        }
        return requestFactory;
    }

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
