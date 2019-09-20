package uk.gov.hmcts.reform.finrem.payments.config;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.finrem.payments.BaseServiceTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class PBAValidationServiceConfigurationTest extends BaseServiceTest {

    @Autowired
    private PBAValidationServiceConfiguration config;

    @Test
    public void shouldCreatePaymentByAccountServiceConfigFromAppProperties() {
        assertThat(config.getUrl(), is("http://localhost:9001"));
        assertThat(config.getApi(), is("/refdata/external/v1/organisations/pbas"));
        assertThat(config.getOldUrl(), is("http://localhost:9002"));
        assertThat(config.getOldApi(), is("/search/pba/"));
        assertThat(config.getLegacyApi(), is("/search/pba/"));
        assertThat(config.isEnableOldUrl(), is(false));
        assertThat(config.isEnableLegacyUrl(), is(false));
    }
}