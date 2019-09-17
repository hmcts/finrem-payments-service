package uk.gov.hmcts.reform.finrem.payments.helper;

import org.junit.Test;
import uk.gov.hmcts.reform.finrem.payments.error.InvalidTokenException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AuthUtilTest {

    @Test
    public void shouldGetBearToken() {
        assertThat(AuthUtil.getBearerToken("aaaa"), is("Bearer aaaa"));
    }

    @Test
    public void shouldReturnGetBearToken() {
        assertThat(AuthUtil.getBearerToken("Bearer aaaa"), is("Bearer aaaa"));
    }

    @Test(expected = InvalidTokenException.class)
    public void shouldReturnBlankToken() {
        assertThat(AuthUtil.getBearerToken(""), is(""));
    }
}