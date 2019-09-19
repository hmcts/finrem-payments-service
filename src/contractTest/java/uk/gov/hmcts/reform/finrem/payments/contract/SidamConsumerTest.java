package uk.gov.hmcts.reform.finrem.payments.contract;


import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.finrem.payments.service.IdamService;

import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "SIDAM_Provider", port = "8888")
@SpringBootTest({
        "idam.url: http://localhost:8888"
    })
@TestPropertySource(locations = "classpath:application-contractTest.properties")
public class SidamConsumerTest {


    @Autowired
    private IdamService idamService;
    private static final String AUTH_TOKEN = "Bearer .someAuthorizationToken";

    @Pact(state = "SIDAM Returns user details",
            provider = "SIDAM_Service", consumer = "finrem_payment_service")
    public RequestResponsePact sidamServicePact(PactDslWithProvider packBuilder) throws JSONException {

        return packBuilder
                .given("Provider haa user details")
                .uponReceiving("GET request user email ")
                .path("/details")
                .method("GET")
                .matchHeader("Authorization",AUTH_TOKEN)
                .matchHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .willRespondWith()
                .status(200)
                .body(idamUserDetailsResponse())
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "sidamServicePact")
    public void verifyIdamUserDetailsRolesPact() {
        String userEmail =  idamService.getUserEmailId(AUTH_TOKEN);
        assertTrue("User is not Admin",userEmail.equals("testforename@test.com"));
    }



    private JSONObject idamUserDetailsResponse() throws JSONException {
        JSONObject details = new JSONObject();
        details.put("email","testforename@test.com");

        return details;
    }
}
