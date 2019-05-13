package uk.gov.hmcts.reform.finrem.payments.integrationtest;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.finrem.payments.PaymentsApplication;
import uk.gov.hmcts.reform.finrem.payments.model.ApplicationType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.FEE_CODE;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.FEE_VERSION;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.PAYMENT_REF;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.PAYMENT_SUCCESS_STATUS;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.PBA_NUMBER;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.feeResponseString;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.pbaAccount;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentRequestStringContent;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponseErrorToString;
import static uk.gov.hmcts.reform.finrem.payments.SetUpUtils.paymentResponseToString;
import static uk.gov.hmcts.reform.finrem.payments.model.ApplicationType.CONSENTED;
import static uk.gov.hmcts.reform.finrem.payments.model.ApplicationType.CONTESTED;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PaymentsApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PropertySource(value = "classpath:application.properties")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PaymentE2ETest {

    private static String MAKE_PAYMENT_API = "/payments/pba-payment";
    private static String FEE_LOOK_UP_API = "/payments/fee-lookup?application-type=";
    private static String PBA_VALIDATE_API = "/payments/pba-validate";

    private static final String AUTH_TOKEN = "Bearer test.auth.token";
    private static final String TEST_SERVICE_AUTH_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
            + ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"
            + ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @Autowired
    private MockMvc webClient;

    @Value("${payment.api}")
    private String paymentApi;

    @Value("${fees.api}")
    private String feeApi;

    @Value("${pba.validation.api}")
    private String pbaApi;

    @Value("${idam.api}")
    private String idamApi;

    @ClassRule
    public static WireMockClassRule serviceAuthProviderServer = new WireMockClassRule(4502);
    @ClassRule
    public static WireMockClassRule paymentsService = new WireMockClassRule(8181);
    @ClassRule
    public static WireMockClassRule feeService = new WireMockClassRule(8182);
    @ClassRule
    public static WireMockClassRule pbaService = new WireMockClassRule(9001);
    @ClassRule
    public static WireMockClassRule idamService = new WireMockClassRule(4501);

    @Test
    public void consentedFeeLookup() throws Exception {
        stubFeeLookUp(CONSENTED);

        webClient.perform(get(FEE_LOOK_UP_API + CONSENTED.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(FEE_CODE)))
                .andExpect(jsonPath("$.version", is(FEE_VERSION)))
                .andExpect(jsonPath("$.fee_amount", is(10)));
    }


    @Test
    public void contestedFeeLookup() throws Exception {
        stubFeeLookUp(CONTESTED);

        webClient.perform(get(FEE_LOOK_UP_API + CONTESTED.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(FEE_CODE)))
                .andExpect(jsonPath("$.version", is(FEE_VERSION)))
                .andExpect(jsonPath("$.fee_amount", is(255)));
    }

    @Test
    public void pbaValidate() throws Exception {
        stubIdamService();
        stubPbaService();

        webClient.perform(get(PBA_VALIDATE_API + "/" + PBA_NUMBER)
                .header("Authorization", AUTH_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pbaNumberValid", is(true)));
    }


    @Test
    public void makePaymentSuccess() throws Exception {
        stubServiceAuthProvider(HttpStatus.OK, TEST_SERVICE_AUTH_TOKEN);
        stubMakePayment(HttpStatus.OK, paymentResponseToString());

        webClient.perform(post(MAKE_PAYMENT_API)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentRequestStringContent()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference", is(PAYMENT_REF)))
                .andExpect(jsonPath("$.status", is(PAYMENT_SUCCESS_STATUS)))
                .andExpect(jsonPath("$.status_histories", hasSize(0)));
    }

    @Test
    public void makePaymentUnsuccessfulDueTo403() throws Exception {
        stubServiceAuthProvider(HttpStatus.OK, TEST_SERVICE_AUTH_TOKEN);
        stubMakePayment(HttpStatus.FORBIDDEN, paymentResponseErrorToString());

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


    private void stubIdamService() {
        idamService.stubFor(WireMock.get(idamApi)
                .withHeader("Authorization", new EqualToPattern(AUTH_TOKEN))
                .withHeader("Content-Type", new EqualToPattern("application/json"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"email\": \"test@email.com\"}")));
    }

    private void stubPbaService() {
        pbaService.stubFor(WireMock.get(pbaValidateUrl())
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(pbaAccount())));
    }

    private void stubFeeLookUp(ApplicationType applicationType) {
        feeService.stubFor(WireMock.get(feeLookupUrl(applicationType))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(feeResponseString(applicationType))));
    }

    private void stubMakePayment(HttpStatus status, String response) {
        paymentsService.stubFor(WireMock.post(paymentApi)
                .withHeader("Authorization", new EqualToPattern(AUTH_TOKEN))
                .withHeader("ServiceAuthorization",
                        new EqualToPattern("Bearer " + TEST_SERVICE_AUTH_TOKEN))
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

    private String pbaValidateUrl() {
        return pbaApi + "test@email.com";
    }

    private String feeLookupUrl(ApplicationType applicationType) {
        if (applicationType == CONSENTED) {
            return feeApi + "?service=other&jurisdiction1=family&jurisdiction2=family-court"
                    + "&channel=default&event=general%20application&keyword=without-notice";
        } else {
            return feeApi + "?service=other&jurisdiction1=family&jurisdiction2=family-court"
                    + "&channel=default&event=miscellaneous&keyword=financial-order";
        }
    }
}
