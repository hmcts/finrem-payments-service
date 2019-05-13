package uk.gov.hmcts.reform.finrem.payments.controllers;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.finrem.payments.model.ApplicationType;
import uk.gov.hmcts.reform.finrem.payments.model.fee.FeeResponse;
import uk.gov.hmcts.reform.finrem.payments.service.FeeService;

import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.finrem.payments.model.ApplicationType.CONSENTED;
import static uk.gov.hmcts.reform.finrem.payments.model.ApplicationType.CONTESTED;

@WebMvcTest(FeeLookupController.class)
public class FeeLookupControllerTest extends BaseControllerTest {
    private static final String FEE_LOOKUP_URL = "/payments/fee-lookup?application-type=";

    @MockBean
    private FeeService feeService;

    private static FeeResponse fee(ApplicationType applicationType) {
        FeeResponse feeResponse = new FeeResponse();
        feeResponse.setCode("FEE0640");
        feeResponse.setDescription("finrem");
        feeResponse.setFeeAmount(applicationType == CONSENTED ? BigDecimal.valueOf(10) : BigDecimal.valueOf(255));
        feeResponse.setVersion("v1");
        return feeResponse;
    }

    private void doFeeLookupSetUp(ApplicationType applicationType) {
        when(feeService.getApplicationFee(applicationType)).thenReturn(fee(applicationType));
    }

    @Test
    public void shouldDoConsentedFeeLookup() throws Exception {
        doFeeLookupSetUp(CONSENTED);
        mvc.perform(get(FEE_LOOKUP_URL + CONSENTED.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("FEE0640")))
                .andExpect(jsonPath("$.description", is("finrem")))
                .andExpect(jsonPath("$.version", is("v1")))
                .andExpect(jsonPath("$.fee_amount", is(10)));
    }


    @Test
    public void shouldDoContestedFeeLookup() throws Exception {
        doFeeLookupSetUp(CONTESTED);
        mvc.perform(get(FEE_LOOKUP_URL + CONTESTED.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("FEE0640")))
                .andExpect(jsonPath("$.description", is("finrem")))
                .andExpect(jsonPath("$.version", is("v1")))
                .andExpect(jsonPath("$.fee_amount", is(255)));
    }

}