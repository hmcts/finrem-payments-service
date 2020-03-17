package uk.gov.hmcts.reform.finrem.payments.controllers;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.finrem.payments.config.ApplicationTypeEnumConverter;
import uk.gov.hmcts.reform.finrem.payments.model.ApplicationType;
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

    @InitBinder
    public void initWebDataBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(ApplicationType.class, new ApplicationTypeEnumConverter());
    }

    @SuppressWarnings("unchecked")
    @ApiOperation("Return fee amount for application type (consented/contested)")
    @GetMapping(path = "/fee-lookup", produces = APPLICATION_JSON)
    public FeeResponse feeLookup(@RequestParam(value = "application-type") ApplicationType applicationType) {
        log.info("Received request for FEE lookup, applicationType = {} ", applicationType);
        return feeService.getApplicationFee(applicationType);
    }

}
