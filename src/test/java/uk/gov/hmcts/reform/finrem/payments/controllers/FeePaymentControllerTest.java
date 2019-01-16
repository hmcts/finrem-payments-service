package uk.gov.hmcts.reform.finrem.payments.controllers;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.finrem.payments.model.fee.Fee;
import uk.gov.hmcts.reform.finrem.payments.service.FeeService;
import uk.gov.hmcts.reform.finrem.payments.service.PBAValidationService;

import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.PAYMENT_REF;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.PAYMENT_STATUS;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentRequest;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponse;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentRequestStringContent;

@WebMvcTest(FeePaymentController.class)
public class FeePaymentControllerTest extends BaseControllerTest {

    private static final String PBA_NUMBER = "PBA123";
    private static final String FEE_LOOK_UP = "/payments/fee-lookup";
    private static final String PBA_VALIDATE_URL = "/payments/pba-validate";
    private static final String PBA_PAYMENT_URL = "/payments/pba-payment";
    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9";

    @MockBean
    private FeeService feeService;

    @MockBean
    private PBAValidationService pbaValidationService;

    @Test
    public void shouldDoFeeLookup() throws Exception {
        when(feeService.getApplicationFee()).thenReturn(fee());

        mvc.perform(get(FEE_LOOK_UP)
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("FEE0640")))
                .andExpect(jsonPath("$.description", is("finrem")))
                .andExpect(jsonPath("$.fee_amount", is(new Integer(10))))
                .andExpect(jsonPath("$.version", is("v1")));
    }

    @Test
    public void shouldReturnBadRequestForMissingPbaNumber() throws Exception {
        mvc.perform(get(PBA_VALIDATE_URL)
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDoPbaValidation() throws Exception {
        when(pbaValidationService.isValidPBA(BEARER_TOKEN, PBA_NUMBER)).thenReturn(true);
        mvc.perform(get(PBA_VALIDATE_URL + "?pbaNumber=" + PBA_NUMBER)
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
        verify(pbaValidationService, times(1)).isValidPBA(BEARER_TOKEN, PBA_NUMBER);
    }

    @Test
    public void shouldMakePayment() throws Exception {
        when(pbaPaymentService.makePayment(BEARER_TOKEN, paymentRequest()))
                .thenReturn(paymentResponse());

        mvc.perform(post(PBA_PAYMENT_URL)
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentRequestStringContent()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference", is(PAYMENT_REF)))
                .andExpect(jsonPath("$.status", is(PAYMENT_STATUS)))
                .andExpect(jsonPath("$.status_histories", hasSize(0)));
    }


    private static Fee fee() {
        Fee fee = new Fee();
        fee.setCode("FEE0640");
        fee.setDescription("finrem");
        fee.setFeeAmount(new BigDecimal("10"));
        fee.setVersion("v1");
        return fee;
    }
}