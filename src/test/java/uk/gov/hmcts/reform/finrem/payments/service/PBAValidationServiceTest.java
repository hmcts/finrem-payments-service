package uk.gov.hmcts.reform.finrem.payments.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.finrem.payments.BaseServiceTest;
import uk.gov.hmcts.reform.finrem.payments.config.PBAValidationServiceConfiguration;
import uk.gov.hmcts.reform.finrem.payments.model.pba.validation.PBAValidationResponse;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class PBAValidationServiceTest extends BaseServiceTest {

    private static final String EMAIL = "test@test.com";
    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9";

    @Autowired
    private PBAValidationService pbaValidationService;

    @MockBean
    private IdamService idamService;

    @MockBean
    private PBAValidationServiceConfiguration pbaValidationServiceConfiguration;

    private JsonNode requestContent;

    @Before
    public void setUp() {
        super.setUp();
        when(pbaValidationServiceConfiguration.getUrl()).thenReturn("http://localhost:9001");
        when(pbaValidationServiceConfiguration.getApi()).thenReturn("/v1/organisations/pbas/");
        when(pbaValidationServiceConfiguration.getOldApi()).thenReturn("/search/pba/");
        when(pbaValidationServiceConfiguration.getLegacyApi()).thenReturn("/search/pba/");
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            requestContent = objectMapper.readTree(new File(getClass()
                    .getResource("/fixtures/payment-by-account.json").toURI()));
        } catch (Exception e) {
            fail(e.getMessage());
        }

        when(idamService.getUserEmailId(AUTH_TOKEN)).thenReturn(EMAIL);
    }

    @Test
    public void pbaNotFound() {
        mockServer.expect(requestTo(toUri()))
                .andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND));

        PBAValidationResponse response = pbaValidationService.isPBAValid(AUTH_TOKEN, "NUM3");
        assertThat(response.isPbaNumberValid(), is(false));
    }


    @Test
    public void validPbaPositive() {
        mockServer.expect(requestTo(toUri()))
                .andExpect(method(GET))
                .andRespond(withSuccess(requestContent.toString(), APPLICATION_JSON));

        PBAValidationResponse response = pbaValidationService.isPBAValid(AUTH_TOKEN, "NUM1");
        assertThat(response.isPbaNumberValid(), is(true));
    }

    @Test
    public void validPbaNegative() {
        mockServer.expect(requestTo(toUri()))
                .andExpect(method(GET))
                .andRespond(withSuccess(requestContent.toString(), APPLICATION_JSON));


        PBAValidationResponse response = pbaValidationService.isPBAValid(AUTH_TOKEN, "NUM3");
        assertThat(response.isPbaNumberValid(), is(false));
    }

    @Test
    public void validPbaNoPbaResult() {
        mockServer.expect(requestTo(toUri()))
                .andExpect(method(GET))
                .andRespond(withSuccess("{\n"
                        + "  \"organisationEntityResponse\": {\n"
                        + "    \"organisationIdentifier\": \"LY7RZOE\",\n"
                        + "    \"name\": \"TestOrg1\",\n"
                        + "    \"status\": \"ACTIVE\",\n"
                        + "    \"sraId\": \"1111\",\n"
                        + "    \"sraRegulated\": true,\n"
                        + "    \"companyNumber\": \"1110111\",\n"
                        + "    \"companyUrl\": \"http://testorg2.co.uk\",\n"
                        + "    \"superUser\": {\n"
                        + "      \"userIdentifier\": \"9503a799-5f4f-4814-8227-776ef5c4dce8\",\n"
                        + "      \"firstName\": \"Henry\",\n"
                        + "      \"lastName\": \"Harper\",\n"
                        + "      \"email\": \"henry_fr_harper@yahoo.com\"\n"
                        + "    },\n"
                        + "    \"paymentAccount\": []\n"
                        + "  }\n"
                        + "}", APPLICATION_JSON));

        PBAValidationResponse response = pbaValidationService.isPBAValid(AUTH_TOKEN, "NUM1");
        assertThat(response.isPbaNumberValid(), is(false));
    }

    private static String toUri() {
        return new StringBuilder("http://localhost:9001/v1/organisations/pbas/?email=")
                .append(EMAIL)
                .toString();
    }

    private static String toOldUri() {
        return new StringBuilder("http://localhost:9002/search/pba/")
                .append(EMAIL)
                .toString();
    }

    private static String toLegacyUri() {
        return new StringBuilder("http://localhost:9001/search/pba/")
                .append(EMAIL)
                .toString();
    }

    @Test
    public void pbaValidationWithOldUrl() {
        when(pbaValidationServiceConfiguration.isEnableOldUrl()).thenReturn(true);
        when(pbaValidationServiceConfiguration.getOldUrl()).thenReturn("http://localhost:9002");
        mockServer.expect(requestTo(toOldUri()))
                .andExpect(method(GET))
                .andRespond(withSuccess(requestContent.toString(), APPLICATION_JSON));
        PBAValidationResponse response = pbaValidationService.isPBAValid(AUTH_TOKEN, "NUM1");
        assertThat(response.isPbaNumberValid(), is(true));
    }

    @Test
    public void pbaNotFoundWithOldUrl() {
        when(pbaValidationServiceConfiguration.isEnableOldUrl()).thenReturn(true);
        when(pbaValidationServiceConfiguration.getOldUrl()).thenReturn("http://localhost:9002");
        mockServer.expect(requestTo(toOldUri()))
                .andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND));

        PBAValidationResponse response = pbaValidationService.isPBAValid(AUTH_TOKEN, "NUM3");
        assertThat(response.isPbaNumberValid(), is(false));
    }

    @Test
    public void pbaValidationWithLegacyUrl() {
        when(pbaValidationServiceConfiguration.isEnableLegacyUrl()).thenReturn(true);
        when(pbaValidationServiceConfiguration.getUrl()).thenReturn("http://localhost:9001");
        mockServer.expect(requestTo(toLegacyUri()))
                .andExpect(method(GET))
                .andRespond(withSuccess(requestContent.toString(), APPLICATION_JSON));
        PBAValidationResponse response = pbaValidationService.isPBAValid(AUTH_TOKEN, "NUM1");
        assertThat(response.isPbaNumberValid(), is(true));
    }

    @Test
    public void pbaNotFoundWithLegacyUrl() {
        when(pbaValidationServiceConfiguration.isEnableLegacyUrl()).thenReturn(true);
        when(pbaValidationServiceConfiguration.getUrl()).thenReturn("http://localhost:9001");
        mockServer.expect(requestTo(toLegacyUri()))
                .andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND));

        PBAValidationResponse response = pbaValidationService.isPBAValid(AUTH_TOKEN, "NUM3");
        assertThat(response.isPbaNumberValid(), is(false));
    }
}