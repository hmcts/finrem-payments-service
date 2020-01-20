package uk.gov.hmcts.reform.finrem.payments.health;

public class PBAServiceHealthCheckTest extends AbstractServiceHealthCheckTest {

    private static final String URI = "http://localhost:9010/health";

    @Override
    protected String uri() {
        return URI;
    }

    @Override
    protected AbstractServiceHealthCheck healthCheckInstance() {
        return new PaymentServiceHealthCheck(URI, restTemplate);
    }
}
