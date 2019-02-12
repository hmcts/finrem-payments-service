package uk.gov.hmcts.reform.finrem.payments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.hmcts.reform.finrem.payments.model.pba.validation.PBAValidationResponse;
import uk.gov.hmcts.reform.finrem.payments.service.PBAValidationService;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/payments")
@Slf4j
@SuppressWarnings("unchecked")
public class PBAValidateController {
    private final PBAValidationService pbaValidationService;

    @SuppressWarnings("unchecked")
    @GetMapping(path = "/pba-validate/{pbaNumber}", produces = APPLICATION_JSON)
    public PBAValidationResponse pbaValidate(
            @RequestHeader(value = "Authorization") String authToken,
            @PathVariable String pbaNumber) {
        log.info("Received request for PBA validate. Auth token: {}, pbaNumber : {}", authToken, pbaNumber);
        return pbaValidationService.isPBAValid(authToken, pbaNumber);
    }

}
