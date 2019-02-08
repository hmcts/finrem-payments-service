package uk.gov.hmcts.reform.finrem.payments.health;

public class PBAServiceHealthCheckTest extends AbstractServiceHealthCheckTest {

    private static final String URI = "http://pba.test/health";

    @Override
    protected String uri() {
        return URI;
    }

    @Override
    protected AbstractServiceHealthCheck healthCheckInstance() {
        return new PBAServiceHealthCheck(URI, restTemplate);
    }
}