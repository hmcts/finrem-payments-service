package uk.gov.hmcts.reform.finrem.payments.error;

public class PaymentException extends RuntimeException{

    public PaymentException(Exception e) {
        super(e);
    }
}
