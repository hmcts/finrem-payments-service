package uk.gov.hmcts.reform.finrem.payments.health;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RunWith(MockitoJUnitRunner.class)
public class PBAServiceHealthCheckTest {

    private static final String NEW_PRD_URI = "http://localhost:9010/health";
    private static final String OLD_PRD_URI = "http://localhost:9011/health";
    private PBAServiceHealthCheck pbaServiceHealthCheck;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void shouldDoHealthCheckWithOldPrdURI() {
        pbaServiceHealthCheck = new PBAServiceHealthCheck(NEW_PRD_URI,
                OLD_PRD_URI, true);
        ReflectionTestUtils.setField(pbaServiceHealthCheck, "restTemplate", restTemplate);
        when(restTemplate.getForEntity(eq(OLD_PRD_URI), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(""));
        assertThat(pbaServiceHealthCheck.health(), is(Health.up().withDetail("uri", OLD_PRD_URI)
                .build()));
    }

    @Test
    public void shouldDoHealthCheckWithNewPrdURI() {
        pbaServiceHealthCheck = new PBAServiceHealthCheck(NEW_PRD_URI,
                OLD_PRD_URI, false);
        ReflectionTestUtils.setField(pbaServiceHealthCheck, "restTemplate", restTemplate);
        when(restTemplate.getForEntity(eq(NEW_PRD_URI), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(""));
        assertThat(pbaServiceHealthCheck.health(), is(Health.up().withDetail("uri", NEW_PRD_URI)
                .build()));
    }

    @Test
    public void shouldDoHealthCheckWithStatusAsDownWithHttpErrorWithNewUri() {
        pbaServiceHealthCheck = new PBAServiceHealthCheck(NEW_PRD_URI,
                OLD_PRD_URI, false);
        ReflectionTestUtils.setField(pbaServiceHealthCheck, "restTemplate", restTemplate);
        doHealthDownTest(new HttpClientErrorException(UNAUTHORIZED), NEW_PRD_URI);
    }

    @Test
    public void shouldDoHealthCheckWithStatusAsDownWithNewUri() {
        pbaServiceHealthCheck = new PBAServiceHealthCheck(NEW_PRD_URI,
                OLD_PRD_URI, false);
        ReflectionTestUtils.setField(pbaServiceHealthCheck, "restTemplate", restTemplate);
        doHealthDownTest(new RuntimeException(), NEW_PRD_URI);
    }

    @Test
    public void shouldDoHealthCheckWithStatusAsDownWithHttpErrorWithOldUri() {
        pbaServiceHealthCheck = new PBAServiceHealthCheck(NEW_PRD_URI,
                OLD_PRD_URI, true);
        ReflectionTestUtils.setField(pbaServiceHealthCheck, "restTemplate", restTemplate);
        doHealthDownTest(new HttpClientErrorException(UNAUTHORIZED), OLD_PRD_URI);
    }

    @Test
    public void shouldDoHealthCheckWithStatusAsDownWithOldUri() {
        pbaServiceHealthCheck = new PBAServiceHealthCheck(NEW_PRD_URI,
                OLD_PRD_URI, true);
        ReflectionTestUtils.setField(pbaServiceHealthCheck, "restTemplate", restTemplate);
        doHealthDownTest(new RuntimeException(), OLD_PRD_URI);
    }

    private void doHealthDownTest(Exception ex, String uri) {

        when(restTemplate.getForEntity(eq(uri), eq(Object.class)))
                .thenThrow(ex);

        assertThat(pbaServiceHealthCheck.health(), is(Health.down().withDetail("uri", uri).withException(ex).build()));

    }
}