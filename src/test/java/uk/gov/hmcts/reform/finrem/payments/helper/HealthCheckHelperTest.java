package uk.gov.hmcts.reform.finrem.payments.helper;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RunWith(MockitoJUnitRunner.class)
public class HealthCheckHelperTest {

    private static final String NEW_PRD_URI = "http://localhost:9010/health";

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void shouldDoHealthCheckConfigurationAndUp() {
        when(restTemplate.getForEntity(eq(NEW_PRD_URI), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(""));
        Health health = HealthCheckHelper.configureRestTemplate(restTemplate, NEW_PRD_URI);
        assertThat(health.getStatus().getCode(), Is.is("UP"));
    }

    @Test
    public void shouldDoHealthCheckConfigurationAndUnknown() {
        when(restTemplate.getForEntity(eq(NEW_PRD_URI), eq(Object.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        Health health = HealthCheckHelper.configureRestTemplate(restTemplate, NEW_PRD_URI);
        assertThat(health.getStatus().getCode(), Is.is("UNKNOWN"));
    }

    @Test
    public void shouldDoHealthCheckConfigurationAndDown() {
        when(restTemplate.getForEntity(eq(NEW_PRD_URI), eq(Object.class)))
                .thenThrow(new HttpServerErrorException(UNAUTHORIZED));
        Health health = HealthCheckHelper.configureRestTemplate(restTemplate, NEW_PRD_URI);
        assertThat(health.getStatus().getCode(), Is.is("DOWN"));
    }

}