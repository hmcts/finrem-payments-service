package uk.gov.hmcts.reform.finrem.payments.service;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import uk.gov.hmcts.reform.finrem.payments.BaseServiceTest;
import uk.gov.hmcts.reform.finrem.payments.model.fee.FeeResponse;

import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static uk.gov.hmcts.reform.finrem.payments.model.ApplicationType.CONSENTED;
import static uk.gov.hmcts.reform.finrem.payments.model.ApplicationType.CONTESTED;

public class FeeServiceTest extends BaseServiceTest {

    @Autowired
    private FeeService feeService;

    @Test
    public void retrieveConsentedFee() {
        mockServer.expect(requestTo(consentedUri()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{ \"code\" : \"FEE0640\", \"fee_amount\" : 50, "
                        + "\"description\" : \"finrem\", \"version\" : \"v1\" }", APPLICATION_JSON));

        FeeResponse feeResponse = feeService.getApplicationFee(CONSENTED);

        MatcherAssert.assertThat(feeResponse.getCode(), Matchers.is("FEE0640"));
        MatcherAssert.assertThat(feeResponse.getDescription(), Matchers.is("finrem"));
        MatcherAssert.assertThat(feeResponse.getVersion(), Matchers.is("v1"));
        MatcherAssert.assertThat(feeResponse.getFeeAmount(), Matchers.is(BigDecimal.valueOf(50)));
    }


    @Test
    public void retrieveContestedFee() {
        mockServer.expect(requestTo(contestedUri()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{ \"code\" : \"FEE0229\", \"fee_amount\" : 255, "
                        + "\"description\" : \"finrem\", \"version\" : \"v1\" }", APPLICATION_JSON));

        FeeResponse feeResponse = feeService.getApplicationFee(CONTESTED);

        MatcherAssert.assertThat(feeResponse.getCode(), Matchers.is("FEE0229"));
        MatcherAssert.assertThat(feeResponse.getDescription(), Matchers.is("finrem"));
        MatcherAssert.assertThat(feeResponse.getVersion(), Matchers.is("v1"));
        MatcherAssert.assertThat(feeResponse.getFeeAmount(), Matchers.is(BigDecimal.valueOf(255)));
    }

    private String consentedUri() {
        return "http://localhost:8182/fees-register/fees/lookup?service=other&jurisdiction1=family&jurisdiction2=family-court&channel=default"
                + "&event=general%20application&keyword=without-notice";
    }

    private String contestedUri() {
        return "http://localhost:8182/fees-register/fees/lookup?service=other&jurisdiction1=family&jurisdiction2=family-court&channel=default"
                + "&event=miscellaneous&keyword=financial-order";
    }

}