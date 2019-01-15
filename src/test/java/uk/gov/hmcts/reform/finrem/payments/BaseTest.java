package uk.gov.hmcts.reform.finrem.payments;

import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PaymentsApplication.class)
public abstract class BaseTest {

    @MockBean
    protected AuthTokenGenerator authTokenGenerator;

    public abstract void setUp();
}
