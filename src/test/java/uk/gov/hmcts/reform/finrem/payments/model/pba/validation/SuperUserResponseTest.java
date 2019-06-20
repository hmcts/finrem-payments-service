package uk.gov.hmcts.reform.finrem.payments.model.pba.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class SuperUserResponseTest {
    private String json = "{\n"
            + "      \"userIdentifier\": \"9503a799-5f4f-4814-8227-776ef5c4dce8\",\n"
            + "      \"firstName\": \"Henry\",\n"
            + "      \"lastName\": \"Harper\",\n"
            + "      \"email\": \"henry_fr_harper@yahoo.com\"\n"
            + "    },\n";

    private SuperUserResponse superUser;

    @Before
    public void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        superUser = mapper.readValue(json, SuperUserResponse.class);
    }

    @Test
    public void shouldPopulateData() {
        assertThat(superUser.getEmail(), Is.is("henry_fr_harper@yahoo.com"));
        assertThat(superUser.getUserIdentifier(), Is.is("9503a799-5f4f-4814-8227-776ef5c4dce8"));
        assertThat(superUser.getFirstName(), Is.is("Henry"));
        assertThat(superUser.getLastName(), Is.is("Harper"));
    }
}