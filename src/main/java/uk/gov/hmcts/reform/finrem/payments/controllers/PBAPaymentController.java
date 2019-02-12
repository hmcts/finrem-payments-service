package uk.gov.hmcts.reform.finrem.payments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentRequest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentResponse;
import uk.gov.hmcts.reform.finrem.payments.service.PBAPaymentService;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/payments")
@Slf4j
public class PBAPaymentController {
    private final PBAPaymentService pbaPaymentService;

    @PostMapping(path = "/pba-payment", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public PaymentResponse pbaPayment(
            @RequestHeader(value = "Authorization") String authToken,
            @RequestBody PaymentRequest paymentRequest) {
        log.info("Received request for PBA payment. Auth token: {}, Payment request : {}", authToken, paymentRequest);
        return pbaPaymentService.makePayment(authToken, paymentRequest);
    }
}
