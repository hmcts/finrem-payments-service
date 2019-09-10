package uk.gov.hmcts.reform.finrem.payments.error;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
