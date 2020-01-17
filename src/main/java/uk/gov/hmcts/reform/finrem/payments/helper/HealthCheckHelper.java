package uk.gov.hmcts.reform.finrem.payments.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class HealthCheckHelper {

    public static Health configureRestTemplate(RestTemplate restTemplate, String uri) {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(uri, Object.class);
            return response.getStatusCode() == (HttpStatus.OK) ? statusHealthy(uri) : statusUnknown(uri);
        } catch (Exception ex) {
            log.error("Exception while checking health on {}, exception: {}", uri, ex);
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
