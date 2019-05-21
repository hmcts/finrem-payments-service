package uk.gov.hmcts.reform.finrem.payments.config;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.finrem.payments.BaseServiceTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FeeServiceConfigurationTest extends BaseServiceTest {

    @Autowired
    private FeeServiceConfiguration config;

    @Test
    public void shouldCreateConsentedFeeServiceConfigFromAppProperties() {
        assertThat(config.getUrl(), is("http://localhost:8182"));
        assertThat(config.getApi(), is("/fees-register/fees/lookup"));
        assertThat(config.getChannel(), is("default"));
        assertThat(config.getJurisdiction1(), is("family"));
        assertThat(config.getJurisdiction2(), is("family-court"));
        assertThat(config.getService(), is("other"));
        assertThat(config.getConsentedEvent(), is("general application"));
        assertThat(config.getConsentedKeyword(), is("without-notice"));
    }


    @Test
    public void shouldCreateContestedFeeServiceConfigFromAppProperties() {
        assertThat(config.getUrl(), is("http://localhost:8182"));
        assertThat(config.getApi(), is("/fees-register/fees/lookup"));
        assertThat(config.getChannel(), is("default"));
        assertThat(config.getJurisdiction1(), is("family"));
        assertThat(config.getJurisdiction2(), is("family-court"));
        assertThat(config.getService(), is("other"));
        assertThat(config.getContestedEvent(), is("miscellaneous"));
        assertThat(config.getContestedKeyword(), is("financial-order"));
    }

}