package uk.gov.hmcts.reform.finrem.payments.health;

public class IdamServiceHealthCheckTest extends AbstractServiceHealthCheckTest {

    private static final String URI = "http://idam/health";

    @Override
    protected String uri() {
        return URI;
    }

    @Override
    protected AbstractServiceHealthCheck healthCheckInstance() {
        return new IdamServiceHealthCheck(URI, restTemplate);
    }
}