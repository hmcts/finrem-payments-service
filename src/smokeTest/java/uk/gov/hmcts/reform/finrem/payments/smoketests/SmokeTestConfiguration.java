package uk.gov.hmcts.reform.finrem.payments.smoketests;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@ComponentScan("uk.gov.hmcts.reform.finrem.payments")
@PropertySource("application.properties")
public class SmokeTestConfiguration {
}
