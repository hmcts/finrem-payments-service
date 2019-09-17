package uk.gov.hmcts.reform.finrem.payments.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import uk.gov.hmcts.reform.finrem.payments.BaseServiceTest;
import uk.gov.hmcts.reform.finrem.payments.error.InvalidTokenException;
import uk.gov.hmcts.reform.finrem.payments.error.PaymentException;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentResponse;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentRequest;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponse;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponseErrorToString;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponseToString;

public class PBAPaymentServiceTest extends BaseServiceTest {

    public static final String AUTH_TOKEN = "Bearer HBJHBKJiuui7097HJH";
    public static final String URI = "http://localhost:8181/credit-account-payments";
    @Autowired
    private PBAPaymentService pbaPaymentService;

    @Test
    public void makePayment() {
        mockServer.expect(requestTo(URI))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(paymentResponseToString(), APPLICATION_JSON));

        PaymentResponse response = pbaPaymentService.makePayment(AUTH_TOKEN, paymentRequest());
        assertThat(response, is(paymentResponse()));
    }

    @Test(expected = PaymentException.class)
    public void makePaymentReceivesClientError() {
        mockServer.expect(requestTo(URI))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withUnauthorizedRequest()
                        .body(paymentResponseErrorToString()).contentType(APPLICATION_JSON));

        pbaPaymentService.makePayment(AUTH_TOKEN, paymentRequest());
    }

    @Test(expected = InvalidTokenException.class)
    public void invalidUserToken() {
        pbaPaymentService.makePayment(null, paymentRequest());
    }
}