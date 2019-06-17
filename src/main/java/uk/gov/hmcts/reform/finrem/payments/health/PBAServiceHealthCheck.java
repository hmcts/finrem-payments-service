package uk.gov.hmcts.reform.finrem.payments.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static uk.gov.hmcts.reform.finrem.payments.helper.HealthCheckHelper.configureRestTemplate;

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
        return configureRestTemplate(restTemplate, uri);
    }

}
