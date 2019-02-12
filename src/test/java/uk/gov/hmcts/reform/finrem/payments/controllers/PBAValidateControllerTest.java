package uk.gov.hmcts.reform.finrem.payments.controllers;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.finrem.payments.model.pba.validation.PBAValidationResponse;
import uk.gov.hmcts.reform.finrem.payments.service.PBAValidationService;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PBAValidateController.class)
public class PBAValidateControllerTest extends BaseControllerTest {

    private static final String PBA_NUMBER = "PBA123";
    private static final String PBA_VALIDATE_URL = "/payments/pba-validate/" + PBA_NUMBER;
    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9";

    @MockBean
    private PBAValidationService pbaValidationService;

    private void doValidatePBASetUp(boolean isPBAValid) {
        PBAValidationResponse response = PBAValidationResponse.builder().pbaNumberValid(isPBAValid).build();
        when(pbaValidationService.isPBAValid(BEARER_TOKEN, PBA_NUMBER)).thenReturn(response);
    }


    @Test
    public void pbaValidationFails() throws Exception {
        doValidatePBASetUp(false);
        mvc.perform(get(PBA_VALIDATE_URL)
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pbaNumberValid", is(false)));
        verify(pbaValidationService, times(1)).isPBAValid(BEARER_TOKEN, PBA_NUMBER);
    }

    @Test
    public void pbaValidationPasses() throws Exception {
        doValidatePBASetUp(true);
        mvc.perform(get(PBA_VALIDATE_URL)
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pbaNumberValid", is(true)));
        verify(pbaValidationService, times(1)).isPBAValid(BEARER_TOKEN, PBA_NUMBER);
    }
}