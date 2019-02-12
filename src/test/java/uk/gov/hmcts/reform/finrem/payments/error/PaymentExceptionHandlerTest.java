package uk.gov.hmcts.reform.finrem.payments.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponseClient404Error;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponseClient401Error;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponseErrorToString;

@RunWith(MockitoJUnitRunner.class)
public class PaymentExceptionHandlerTest {
    private static Charset UTF8 = Charset.forName("UTF-8");

    @Spy
    private final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private PaymentExceptionHandler exceptionHandler;

    @Test
    public void handleClient404Exception() {
        ResponseEntity<Object> response = exceptionHandler.handlePaymentException(paymentApi404Error());

        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody(), is(paymentResponseClient404Error()));
    }

    @Test
    public void handleClient401Exception() {
        ResponseEntity<Object> response = exceptionHandler.handlePaymentException(paymentApi401Error());

        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody(), is(paymentResponseClient401Error()));
    }

    @Test
    public void handleClient403ValidationException() {
        ResponseEntity<Object> response = exceptionHandler.handlePaymentException(paymentApi403ValidationError());

        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody(), is(paymentResponseClient401Error()));
    }

    @Test
    public void handleServerException() {
        ResponseEntity<Object> response = exceptionHandler.handlePaymentException(paymentApiServerError());

        assertThat(response.getStatusCode(), is(INTERNAL_SERVER_ERROR));
    }


    private PaymentException paymentApiServerError() {
        return new PaymentException(new HttpServerErrorException(SERVICE_UNAVAILABLE));
    }

    private PaymentException paymentApi404Error() {
        return new PaymentException(
                new HttpClientErrorException(NOT_FOUND, "",
                        "Account information could not be found".getBytes(), UTF8)
        );
    }

    private PaymentException paymentApi401Error() {
        return new PaymentException(
                new HttpClientErrorException(FORBIDDEN, "",
                        paymentResponseErrorToString().getBytes(), UTF8)
        );
    }

    private PaymentException paymentApi403ValidationError() {
        return new PaymentException(
                new HttpClientErrorException(FORBIDDEN, "",
                        paymentResponseErrorToString().getBytes(), UTF8)
        );
    }

}