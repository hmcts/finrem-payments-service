package uk.gov.hmcts.reform.finrem.payments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.hmcts.reform.finrem.payments.model.fee.Fee;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentRequest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentResponse;
import uk.gov.hmcts.reform.finrem.payments.service.FeeService;
import uk.gov.hmcts.reform.finrem.payments.service.PBAPaymentService;
import uk.gov.hmcts.reform.finrem.payments.service.PBAValidationService;

import javax.ws.rs.core.MediaType;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/payments")
@Slf4j
public class FeePaymentController {
    private final FeeService feeService;
    private final PBAValidationService pbaValidationService;
    private final PBAPaymentService pbaPaymentService;

    @GetMapping(path = "/fee-lookup", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public Fee feeLookup(
            @RequestHeader(value = "Authorization", required = false) String authToken) {
        log.info("Received request for FEE lookup. Auth token: {}", authToken);
        return feeService.getApplicationFee();
    }

    @GetMapping(
            path = "/pba-validate",
            produces = MediaType.APPLICATION_JSON)
    public Boolean pbaValidate(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestParam("pbaNumber") String pbaNumber) {
        log.info("Received request for PBA validate. Auth token: {}, PBA Number : {}", authToken, pbaNumber);
        return pbaValidationService.isValidPBA(authToken, pbaNumber);
    }

    @PostMapping(
            path = "/pba-payment",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON)
    public PaymentResponse pbaPayment(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestBody PaymentRequest paymentRequest) {
        log.info("Received request for PBA payment. Auth token: {}, Payment request : {}", authToken, paymentRequest);
        return pbaPaymentService.makePayment(authToken, paymentRequest);
    }
}
