package uk.gov.hmcts.reform.finrem.payments.health;

import static org.junit.Assert.*;

public class FeesServiceHealthCheckTest extends AbstractServiceHealthCheckTest {

    private static final String URI = "http://fees.test/health";

    @Override
    protected String uri() {
        return URI;
    }

    @Override
    protected AbstractServiceHealthCheck healthCheckInstance() {
        return new FeesServiceHealthCheck(URI, restTemplate);
    }
}