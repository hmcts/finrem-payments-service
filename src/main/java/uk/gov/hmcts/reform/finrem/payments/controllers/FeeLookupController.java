package uk.gov.hmcts.reform.finrem.payments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.finrem.payments.model.fee.FeeResponse;
import uk.gov.hmcts.reform.finrem.payments.service.FeeService;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/payments")
@Slf4j
@SuppressWarnings("unchecked")
public class FeeLookupController {
    private final FeeService feeService;

    @SuppressWarnings("unchecked")
    @GetMapping(path = "/fee-lookup", produces = APPLICATION_JSON)
    public FeeResponse feeLookup() {
        log.info("Received request for FEE lookup.");
        return feeService.getApplicationFee();
    }

}
