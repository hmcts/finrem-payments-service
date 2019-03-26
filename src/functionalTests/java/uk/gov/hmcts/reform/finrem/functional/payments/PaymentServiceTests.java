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
import static uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentResponse.PAYMENT_STATUS_SUCCESS;

@RunWith(SerenityRunner.class)
public class PaymentServiceTests extends IntegrationTestBase {
    private static String FEE_LOOKUP = "/payments/fee-lookup";
    private static String PBA_VALIDATE = "/payments/pba-validate/";
    private static String PBA_PAYMENT = "/payments/pba-payment";
    private HashMap<String, String> pbaAccounts = new HashMap<>();


    @Value("${payment_api_url}")
    private String pbaValidationUrl;

    @Value("${idam.s2s-auth.microservice}")
    private String microservice;

    @Value("${idam.s2s-auth.secret}")
    private String authClientSecret;

    @Value("${pdf_access_key}")
    private String pdfAccessKey;

    @Value("${pba.account.active}")
    private String pbaAccountActive;

    @Value("${pba.account.inactive}")
    private String pbaAccountInActive;

    @Value("${pba.account.liberata.check.enabled}")
    private boolean pbaAccountLiberataCheckEnabled;


    @Test
    public void verifyPBAAccountStatus() {
        pbaAccounts.put(pbaAccountActive, "Active");
        pbaAccounts.put(pbaAccountInActive, "Inactive");

        validatePBAAccountNumber(PBA_VALIDATE, pbaAccounts);


    }

    @Test
    public void verifyGetFeeLoopUpTest() {

        validatePostSuccess(FEE_LOOKUP);

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

        System.out.println("PBA Validation : " + pbaValidationUrl + url + "PBA0066906");

        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeader())
                .when().get(pbaValidationUrl + url + "PBA0066906")
                .then()
                .assertThat().statusCode(200);
    }

    private void validateFailurePBAPayment(String url) {

        System.out.println("PBA Payment : " + pbaValidationUrl + url);
        Response response = getPBAPaymentResponse("FailurePaymentRequestPayload.json", pbaValidationUrl + url);
        int statusCode = response.getStatusCode();
        JsonPath jsonPathEvaluator = response.jsonPath();

        assertEquals(statusCode, 200);

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
        System.out.println("Response Body : " + response.getBody().prettyPrint());
        //assertEquals(statusCode, 200);
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

            JsonPath jsonPathEvaluator = response.jsonPath();
            System.out.println("Reponse Body : " + response.getBody().prettyPrint());

            if (status.equalsIgnoreCase("Active")) {

                assertTrue(jsonPathEvaluator.get("pbaNumberValid").toString()
                        .equalsIgnoreCase("true"));

            } else if (status.equalsIgnoreCase("Inactive")) {

                assertTrue(jsonPathEvaluator.get("pbaNumberValid").toString()
                        .equalsIgnoreCase("false"));
            }

        });
    }
}


