package uk.gov.hmcts.reform.finrem.payments.config;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.finrem.payments.BaseServiceTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PBAValidationServiceConfigurationTest extends BaseServiceTest {

    @Autowired
    private PBAValidationServiceConfiguration config;

    @Test
    public void shouldCreatePaymentByAccountServiceConfigFromAppProperties() {
        assertThat(config.getUrl(), is("http://localhost:9001"));
        assertThat(config.getApi(), is("/refdata/external/v1/organisations/pbas"));
    }
}
