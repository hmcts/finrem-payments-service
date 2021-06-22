package uk.gov.hmcts.reform.finrem.payments.contract;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Executor;
import org.json.JSONException;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.finrem.payments.BaseContractTest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentRequest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentResponse;
import uk.gov.hmcts.reform.finrem.payments.service.PBAPaymentService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.junit.Assert.assertEquals;

@SpringBootTest({"payment.url: http://localhost:8886"})
@TestPropertySource(locations = "classpath:application-contractTest.properties")
@PactFolder("pacts")
public class PBAPaymentConsumerSuccessTest extends BaseContractTest {

    public static final String AUTH_TOKEN = "Bearer someAuthorizationToken";

    @Autowired
    PBAPaymentService pbaPaymentService;

    @Autowired
    ObjectMapper objectMapper;

    @Rule
    public PactProviderRule mockProvider = new PactProviderRule("payment_creditAccountPayment", "localhost", 8886, this);

    @After
    public void teardown() {
        Executor.closeIdleConnections();
    }

    @Pact(provider = "payment_creditAccountPayment", consumer = "fr_paymentService")
    public RequestResponsePact generatePactFragmentSuccess(PactDslWithProvider builder) throws JSONException, IOException {

        Map<String, Object> paymentMap = new HashMap<>();
        paymentMap.put("accountNumber", "test.account");
        paymentMap.put("availableBalance", "1000.00");
        paymentMap.put("accountName", "test.account.name");

        return builder
                .given("An active account has sufficient funds for a payment", paymentMap)
                .uponReceiving("A request for a payment")
                .path("/credit-account-payments")
                .method("POST")
                .headers(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .body(objectMapper.writeValueAsString(getPaymentRequestSuccess(BigDecimal.TEN)))
                .willRespondWith()
                .status(201)
                .body(buildPBAPaymentResponseDsl("Success", "success", null, "Insufficient funds available"))
                .toPact();
    }

    @Test
    @PactVerification(fragment = "generatePactFragmentSuccess")
    public void verifyPBAPaymentPactSuccess() {
        PaymentResponse paymentResponse = pbaPaymentService.makePayment(AUTH_TOKEN, getPaymentRequestSuccess(BigDecimal.TEN));
        assertEquals("reference", paymentResponse.getReference());
    }

    private DslPart buildPBAPaymentResponseDsl(String status, String paymentStatus, String errorCode, String errorMessage) {
        return getDslPart(status, paymentStatus, errorCode, errorMessage);
    }

    static DslPart getDslPart(String status, String paymentStatus, String errorCode, String errorMessage) {
        return newJsonBody((o) -> {
            o.stringType("reference", "reference")
                    .stringType("status", status)
                    .minArrayLike("status_histories", 1, 1,
                        (sh) -> {
                            sh.stringMatcher("date_updated",
                                    "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}\\+\\d{4})$",
                                    "2020-10-06T18:54:48.785+0000")
                                    .stringMatcher("date_created",
                                            "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}\\+\\d{4})$",
                                            "2020-10-06T18:54:48.785+0000")
                                    .stringValue("status", paymentStatus);
                            if (errorCode != null) {
                                sh.stringValue("error_code", errorCode);
                                sh.stringType("error_message",
                                        errorMessage);
                            }
                        });
        }).build();
    }

    private PaymentRequest getPaymentRequestSuccess(BigDecimal amount) {
        return getPaymentRequest(amount);
    }

    @Override
    public void setUp() {
    }
}
