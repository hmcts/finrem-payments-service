package uk.gov.hmcts.reform.finrem.payments.health;

public class PaymentServiceHealthCheckTest extends AbstractServiceHealthCheckTest {

    private static final String URI = "http://payment.test/health";

    @Override
    protected String uri() {
        return URI;
    }

    @Override
    protected AbstractServiceHealthCheck healthCheckInstance() {
        return new PaymentServiceHealthCheck(URI, restTemplate);
    }
}