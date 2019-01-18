package uk.gov.hmcts.reform.finrem.payments.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponseClientError;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponseErrorToString;

@RunWith(MockitoJUnitRunner.class)
public class PaymentExceptionHandlerTest {

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private PaymentExceptionHandler paymentExceptionHandler;

    @Test
    public void handleClientException() {
        ResponseEntity<Object> response = paymentExceptionHandler.handlePaymentException(paymentApi401Error());

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(paymentResponseClientError()));
    }

    @Test
    public void handleServerException() {
        ResponseEntity<Object> response = paymentExceptionHandler.handlePaymentException(paymentApiServerError());

        assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private PaymentException paymentApiServerError() {
        return new PaymentException(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE));
    }

    private PaymentException paymentApi401Error() {
        return new PaymentException(
                new HttpClientErrorException(HttpStatus.FORBIDDEN, "",
                        paymentResponseErrorToString().getBytes(),
                        Charset.forName("UTF8"))
        );
    }
}