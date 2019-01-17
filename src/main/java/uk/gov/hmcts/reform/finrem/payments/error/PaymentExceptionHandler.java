package uk.gov.hmcts.reform.finrem.payments.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentResponse;

import java.io.IOException;

@ControllerAdvice
@Slf4j
public class PaymentExceptionHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<Object> handlePaymentException(PaymentException exception) {
        log.error("exception occurred while making payment ", exception.getMessage());

        if (exception.getCause() != null && exception.getCause() instanceof HttpClientErrorException) {
            HttpClientErrorException cause = (HttpClientErrorException) exception.getCause();
            try {
                return ResponseEntity.ok(objectMapper.readValue(cause.getResponseBodyAsString(), PaymentResponse.class));
            } catch (IOException e) {
                log.error("payment-error-conversion exception : {} ", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}
