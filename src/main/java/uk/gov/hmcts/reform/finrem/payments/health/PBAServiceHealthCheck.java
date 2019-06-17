package uk.gov.hmcts.reform.finrem.payments.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class PBAServiceHealthCheck implements HealthIndicator {


    private String uri;
    @Autowired
    private RestTemplate restTemplate;

    public PBAServiceHealthCheck(@Value("${pba.validation.health.url}") String newUri,
                                 @Value("${pba.validation.old.health.url}") String oldUri,
                                 @Value("${pba.validation.old.url.enabled}") boolean oldUrlEnabled) {
        this.uri = (oldUrlEnabled ? oldUri : newUri);
    }


    /**
     * Return an indication of health.
     *
     * @return the health for Fees service
     */
    @Override
    public Health health() {
        try {
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
            messageConverters.add(converter);
            restTemplate.setMessageConverters(messageConverters);
            ResponseEntity<Object> response = restTemplate.getForEntity(uri, Object.class);
            return response.getStatusCode() == (HttpStatus.OK) ? statusHealthy() : statusUnknown();
        } catch (HttpStatusCodeException ex) {
            log.error("Http exception occurred while doing health check on", ex);
            return statusError(ex);
        } catch (Exception ex) {
            log.error("Unknown exception occurred while doing health check", ex);
            return statusError(ex);
        }
    }

    private Health statusError(Exception ex) {
        return Health.down().withDetail("uri", uri).withException(ex).build();
    }

    private Health statusHealthy() {
        return Health.up().withDetail("uri", uri).build();
    }

    private Health statusUnknown() {
        return Health.unknown().withDetail("uri", uri).build();
    }

}
