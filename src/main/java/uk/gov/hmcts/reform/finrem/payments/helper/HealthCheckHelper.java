package uk.gov.hmcts.reform.finrem.payments.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class HealthCheckHelper {

    public static Health configureRestTemplate(RestTemplate restTemplate, String uri) {
        try {
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setSupportedMediaTypes(Arrays.asList(MediaType.ALL));
            messageConverters.add(converter);
            restTemplate.setMessageConverters(messageConverters);
            ResponseEntity<Object> response = restTemplate.getForEntity(uri, Object.class);
            return response.getStatusCode() == (HttpStatus.OK) ? statusHealthy(uri) : statusUnknown(uri);
        } catch (HttpStatusCodeException ex) {
            log.error("Http exception occurred while doing health check on", ex);
            return statusError(ex, uri);
        } catch (Exception ex) {
            log.error("Unknown exception occurred while doing health check", ex);
            return statusError(ex, uri);
        }
    }

    private static Health statusHealthy(String uri) {
        return Health.up().withDetail("uri", uri).build();
    }

    private static Health statusError(Exception ex, String uri) {
        return Health.down().withDetail("uri", uri).withException(ex).build();
    }

    private static Health statusUnknown(String uri) {
        return Health.unknown().withDetail("uri", uri).build();
    }
}
