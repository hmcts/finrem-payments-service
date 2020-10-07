package uk.gov.hmcts.reform.finrem.payments.error;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.STATUS_CODE;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.feignError;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.invalidTokenException;

public class GlobalExceptionHandlerTest {

    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    public void handleFeignException() {
        ResponseEntity<Object> actual = exceptionHandler.handleFeignException(feignError());
        assertThat(actual.getStatusCodeValue(), Matchers.is(STATUS_CODE));
    }

    @Test
    public void handleInvalidTokenException() {
        ResponseEntity<Object> actual = exceptionHandler.handleInvalidException(invalidTokenException());
        assertThat(actual.getStatusCodeValue(), Matchers.is(BAD_REQUEST.value()));
    }
}