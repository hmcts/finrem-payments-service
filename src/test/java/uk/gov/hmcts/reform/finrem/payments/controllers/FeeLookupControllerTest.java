package uk.gov.hmcts.reform.finrem.payments.controllers;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.finrem.payments.model.fee.FeeResponse;
import uk.gov.hmcts.reform.finrem.payments.service.FeeService;

import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeeLookupController.class)
public class FeeLookupControllerTest extends BaseControllerTest {
    private static final String FEE_LOOKUP_URL = "/payments/fee-lookup";

    @MockBean
    private FeeService feeService;

    private static FeeResponse fee() {
        FeeResponse feeResponse = new FeeResponse();
        feeResponse.setCode("FEE0640");
        feeResponse.setDescription("finrem");
        feeResponse.setFeeAmount(BigDecimal.valueOf(10d));
        feeResponse.setVersion("v1");
        return feeResponse;
    }

    private void doFeeLookupSetUp() {
        when(feeService.getApplicationFee()).thenReturn(fee());
    }

    @Test
    public void shouldDoFeeLookup() throws Exception {
        doFeeLookupSetUp();
        mvc.perform(get(FEE_LOOKUP_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("FEE0640")))
                .andExpect(jsonPath("$.description", is("finrem")))
                .andExpect(jsonPath("$.version", is("v1")))
                .andExpect(jsonPath("$.fee_amount", is(10.0)));
    }

}