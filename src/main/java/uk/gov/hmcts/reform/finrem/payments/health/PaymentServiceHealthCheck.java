package uk.gov.hmcts.reform.finrem.payments.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentServiceHealthCheck extends AbstractServiceHealthCheck {

    public PaymentServiceHealthCheck(@Value("${payment.health.url}") String uri, RestTemplate restTemplate) {
        super(uri, restTemplate);
    }
}
