package uk.gov.hmcts.reform.finrem.payments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.finrem.payments.model.pba.validation.PBAAccount;

import java.util.Arrays;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/search")
@Slf4j
public class PBAValidationStub {

    @GetMapping(path = "/pba/{emailId}", produces = APPLICATION_JSON)
    public PBAAccount pbaList(@PathVariable String emailId) {
        log.info("Received request for PBA check, emailId : {}", emailId);
        return PBAAccount.builder().accountList(Arrays.asList("PBA123", "PBA456")).build();
    }

}
