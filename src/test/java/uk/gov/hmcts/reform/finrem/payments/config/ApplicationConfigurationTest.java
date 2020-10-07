package uk.gov.hmcts.reform.finrem.payments.config;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import uk.gov.hmcts.reform.finrem.payments.BaseServiceTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ApplicationConfigurationTest extends BaseServiceTest {

    @Autowired
    private ApplicationConfiguration applicationConfiguration;

    @Test
    public void createRestTemplate() throws Exception {
        assertThat(applicationConfiguration.restTemplate(), is(notNullValue()));
    }

    @Test
    public void createObjectMapper() {
        assertThat(applicationConfiguration.objectMapper(Jackson2ObjectMapperBuilder.json()), is(notNullValue()));
    }
}