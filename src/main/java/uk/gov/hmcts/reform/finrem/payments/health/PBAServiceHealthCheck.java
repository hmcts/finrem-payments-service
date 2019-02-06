package uk.gov.hmcts.reform.finrem.payments.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PBAServiceHealthCheck extends AbstractServiceHealthCheck {

    public PBAServiceHealthCheck(@Value("${pba.validation.health.url}") String uri, RestTemplate restTemplate) {
        super(uri, restTemplate);
    }
}
