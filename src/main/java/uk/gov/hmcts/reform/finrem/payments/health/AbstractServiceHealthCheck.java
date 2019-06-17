package uk.gov.hmcts.reform.finrem.payments.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.client.RestTemplate;

import static uk.gov.hmcts.reform.finrem.payments.helper.HealthCheckHelper.configureRestTemplate;

@Slf4j
public abstract class AbstractServiceHealthCheck implements HealthIndicator {

    private final String uri;
    private final RestTemplate restTemplate;

    public AbstractServiceHealthCheck(String uri, RestTemplate restTemplate) {
        this.uri = uri;
        this.restTemplate = restTemplate;
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
