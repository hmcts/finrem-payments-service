package uk.gov.hmcts.reform.finrem.payments.config;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.finrem.payments.BaseServiceTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HttpConfigurationTest extends BaseServiceTest {

    @Autowired
    private HttpConfiguration httpConfiguration;

    @Test
    public void shouldCreateHttpConfigFromAppProperties() {
        assertThat(httpConfiguration.getTimeout(), is(-1));
        assertThat(httpConfiguration.getRequestTimeout(), is(-1));
        assertThat(httpConfiguration.getReadTimeout(), is(-1));
    }
}
