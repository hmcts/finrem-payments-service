package uk.gov.hmcts.reform.finrem.payments.e2etest;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.finrem.payments.PaymentsApplication;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.PAYMENT_REF;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.PAYMENT_STATUS;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentRequestStringContent;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponseErrorToString;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponseToString;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PaymentsApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PropertySource(value = "classpath:application.properties")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PaymentE2ETest {

    public static final String AUTH_TOKEN = "Bearer test.auth.token";
    private static String MAKE_PAYMENT_API = "/payments/pba-payment";
    private static final String TEST_SERVICE_AUTH_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
            + ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"
            + ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @Autowired
    private MockMvc webClient;

    @Value("${payment.api}")
    private String paymentApi;

    @ClassRule
    public static WireMockClassRule serviceAuthProviderServer = new WireMockClassRule(4502);

    @ClassRule
    public static WireMockClassRule paymentsService = new WireMockClassRule(8181);

    @Test
    public void makePaymentSuccess() throws Exception {
        stubServiceAuthProvider(HttpStatus.OK, TEST_SERVICE_AUTH_TOKEN);
        stubCreditAccountPayment(HttpStatus.OK, paymentResponseToString());

        webClient.perform(post(MAKE_PAYMENT_API)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentRequestStringContent()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference", is(PAYMENT_REF)))
                .andExpect(jsonPath("$.status", is(PAYMENT_STATUS)))
                .andExpect(jsonPath("$.status_histories", hasSize(0)));
    }

    @Test
    public void makePaymentUnsuccessfulDueTo403() throws Exception {
        stubServiceAuthProvider(HttpStatus.OK, TEST_SERVICE_AUTH_TOKEN);
        stubCreditAccountPayment(HttpStatus.FORBIDDEN, paymentResponseErrorToString());

        webClient.perform(post(MAKE_PAYMENT_API)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentRequestStringContent()))
                .andExpect(status().isOk());
    }

    @Test
    public void makePaymentFailsDueToServiceAuthError() throws Exception {
        stubServiceAuthProvider(HttpStatus.UNAUTHORIZED, "");

        webClient.perform(post(MAKE_PAYMENT_API)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentRequestStringContent()))
                .andExpect(status().isUnauthorized());
    }

    private void stubCreditAccountPayment(HttpStatus status, String response) {
        paymentsService.stubFor(WireMock.post(paymentApi)
                .withHeader("Authorization", new EqualToPattern(AUTH_TOKEN))
                .withHeader("ServiceAuthorization", new EqualToPattern("Bearer "+ TEST_SERVICE_AUTH_TOKEN))
                .withRequestBody(equalToJson(paymentRequestStringContent()))
                .willReturn(aResponse()
                        .withStatus(status.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    private void stubServiceAuthProvider(HttpStatus status, String response) {
        serviceAuthProviderServer.stubFor(WireMock.post("/lease")
                .willReturn(aResponse()
                        .withStatus(status.value())
                        .withBody(response)));
    }
}
