package uk.gov.hmcts.reform.finrem.functional.payments;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.finrem.functional.IntegrationTestBase;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.hmcts.reform.finrem.payments.model.ApplicationType.CONSENTED;
import static uk.gov.hmcts.reform.finrem.payments.model.ApplicationType.CONTESTED;
import static uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentResponse.PAYMENT_STATUS_SUCCESS;

@RunWith(SerenityRunner.class)
public class PaymentServiceTests extends IntegrationTestBase {

    private static final String FEE_LOOKUP = "/payments/fee-lookup?application-type=";
    private static final String PBA_VALIDATE = "/payments/pba-validate/";
    private static final String PBA_PAYMENT = "/payments/pba-payment";
    private static final String VALID = "Valid";
    private static final String INVALID = "Invalid";

    private HashMap<String, String> pbaAccounts = new HashMap<>();

    @Value("${payment_api_url}")
    private String pbaValidationUrl;

    @Value("${pba.account.valid}")
    private String pbaAccountValid;

    @Value("${pba.account.invalid}")
    private String pbaAccountInvalid;

    @Value("${pba.account.liberata.check.enabled}")
    private boolean pbaAccountLiberataCheckEnabled;

    @Test
    public void verifyPBAAccountStatus() {
        pbaAccounts.put(pbaAccountValid, VALID);
        pbaAccounts.put(pbaAccountInvalid, INVALID);

        validatePBAAccountNumber(PBA_VALIDATE, pbaAccounts);
    }

    @Test
    public void verifyConsentedFeeLoopUpTest() {
        validatePostSuccess(FEE_LOOKUP + CONSENTED.toString());
    }


    @Test
    public void verifyContestedFeeLoopUpTest() {
        validatePostSuccess(FEE_LOOKUP + CONTESTED.toString());
    }

    @Test
    public void verifyPBAValidationTest() {
        validatePostSuccessForPBAValidation(PBA_VALIDATE);
    }

    @Test
    public void verifyPBAPaymentSuccessTest() {
        validatePostSuccessForPBAPayment(PBA_PAYMENT);
    }

    @Test
    public void verifyPBAPaymentFailureTest() {
        validateFailurePBAPayment(PBA_PAYMENT);

    }

    private void validatePostSuccess(String url) {
        System.out.println("Fee LookUp : " + pbaValidationUrl + url);

        SerenityRest.given()
                .relaxedHTTPSValidation()
                .when().get(pbaValidationUrl + url)
                .then()
                .assertThat().statusCode(200);
    }

    private void validatePostSuccessForPBAValidation(String url) {
        System.out.println("PBA Validation : " + pbaValidationUrl + url + "PBA0087935");

        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeader())
                .when().get(pbaValidationUrl + url + "PBA0087935")
                .then()
                .assertThat().statusCode(200);
    }

    private void validateFailurePBAPayment(String url) {

        System.out.println("PBA Payment : " + pbaValidationUrl + url);
        Response response = getPBAPaymentResponse("FailurePaymentRequestPayload.json", pbaValidationUrl + url);
        int statusCode = response.getStatusCode();
        JsonPath jsonPathEvaluator = response.jsonPath();

        assertEquals(200, statusCode);

        if (pbaAccountLiberataCheckEnabled) {
            assertTrue(jsonPathEvaluator.get("paymentError").toString()
                    .equalsIgnoreCase("Account information could not be found"));

            assertTrue(jsonPathEvaluator.get("error").toString()
                    .equalsIgnoreCase("404"));
        }
    }

    private void validatePostSuccessForPBAPayment(String url) {
        System.out.println("PBA Payment : " + pbaValidationUrl + url);
        Response response = getPBAPaymentResponse("SuccessPaymentRequestPayload.json", pbaValidationUrl + url);
        int statusCode = response.getStatusCode();
        JsonPath jsonPathEvaluator = response.jsonPath();

        assertEquals(200, statusCode);

        assertTrue(PAYMENT_STATUS_SUCCESS.contains(jsonPathEvaluator.get("status").toString().toLowerCase()));
    }

    private Response getPBAPaymentResponse(String payload, String url) {

        return SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeader())
                .contentType("application/json")
                .body(utils.getJsonFromFile(payload))
                .when().post(url)
                .andReturn();
    }

    private void validatePBAAccountNumber(String url, HashMap<String, String> pbaAccount) {
        pbaAccount.forEach((account, status) -> {
            Response response = SerenityRest.given()
                    .relaxedHTTPSValidation()
                    .headers(utils.getHeader())
                    .when().get(pbaValidationUrl + url + account).andReturn();

            assertTrue(response.jsonPath().get("pbaNumberValid").toString().equalsIgnoreCase(
                VALID.equals(status) ? "true" : "false"));
        });
    }
}
