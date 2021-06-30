package uk.gov.hmcts.reform.finrem.payments.config;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.finrem.payments.BaseServiceTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FeeServiceConfigurationTest extends BaseServiceTest {

    @Autowired
    private FeeServiceConfiguration config;

    @Value("${fees.consented-keyword}")
    private String consentedFeeKeyword;

    @Test
    public void shouldCreateConsentedFeeServiceConfigFromAppProperties() {
        assertThat(config.getUrl(), is("http://localhost:8182"));
        assertThat(config.getApi(), is("/fees-register/fees/lookup"));
        assertThat(config.getChannel(), is("default"));
        assertThat(config.getJurisdiction1(), is("family"));
        assertThat(config.getJurisdiction2(), is("family-court"));
        assertThat(config.getService(), is("other"));
        assertThat(config.getConsentedEvent(), is("general application"));
        assertThat(config.getConsentedKeyword(), is(consentedFeeKeyword));
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
        assertThat(config.getContestedKeyword(), is("FinancialOrderOnNotice"));
    }
}
