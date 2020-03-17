package uk.gov.hmcts.reform.finrem.payments.controllers;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.PAYMENT_REF;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.PAYMENT_SUCCESS_STATUS;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentRequest;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentRequestStringContent;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponse;

@WebMvcTest(PBAPaymentController.class)
public class PBAPaymentControllerTest extends BaseControllerTest {

    private static final String PBA_PAYMENT_URL = "/payments/pba-payment";
    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9";

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
                .andExpect(jsonPath("$.status", is(PAYMENT_SUCCESS_STATUS)))
                .andExpect(jsonPath("$.status_histories", hasSize(0)));
    }
}