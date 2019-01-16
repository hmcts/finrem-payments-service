package uk.gov.hmcts.reform.finrem.payments.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.finrem.payments.BaseServiceTest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentResponse;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentRequest;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponse;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponseStringContent;

public class PBAPaymentServiceTest extends BaseServiceTest {

    @Autowired
    private PBAPaymentService pbaPaymentService;

    @Test
    public void makePayment() {
        mockServer.expect(requestTo("http://payment-test.internal/credit-account-payments"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(paymentResponseStringContent(), MediaType.APPLICATION_JSON));

        PaymentResponse response = pbaPaymentService.makePayment("HBJHBKJiuui7097HJH", paymentRequest());
        assertThat(response, is(paymentResponse()));
    }
}