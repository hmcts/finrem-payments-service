package uk.gov.hmcts.reform.finrem.payments.config;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.finrem.payments.BaseServiceTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class IdamServiceConfigurationTest extends BaseServiceTest {

    @Autowired
    private IdamServiceConfiguration config;

    @Test
    public void shouldCreateIdamServiceConfigFromAppProperties() {
        assertThat(config.getUrl(), is("http://localhost:4501"));
        assertThat(config.getApi(), is("/details"));
    }
}