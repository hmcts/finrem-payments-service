package uk.gov.hmcts.reform.finrem.payments.config;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.finrem.payments.BaseServiceTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HttpConfigurationTest extends BaseServiceTest {

    @Autowired
    private HttpConfiguration httpConfiguration;

    @Test
    public void shouldCreateHttpConfigFromAppProperties() {
        assertThat(httpConfiguration.getReadTimeout(), is(60000));
        assertThat(httpConfiguration.getRequestTimeout(), is(60000));
        assertThat(httpConfiguration.getReadTimeout(), is(60000));
    }
}