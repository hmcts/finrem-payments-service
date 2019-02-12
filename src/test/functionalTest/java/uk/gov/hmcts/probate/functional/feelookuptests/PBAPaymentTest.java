package uk.gov.hmcts.probate.functional.feelookuptests;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static org.hamcrest.Matchers.containsString;

@RunWith(SerenityRunner.class)
public class PBAPaymentTest extends IntegrationTestBase {

    private static final String MAKE_PAYMENT_URL = "/payments/pba-payment";

    @Test
    public void verifyEmptyRequest() {
        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeaders())
                .body("")
                .when().post(MAKE_PAYMENT_URL).then().statusCode(400)
                .and().body("message", containsString("Missing request header 'Authorization'"));

    }

}
