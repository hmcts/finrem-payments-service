package uk.gov.hmcts.reform.finrem.payments.model.pba.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DxAddressResponseTest {
    private String json = "{\"dxNumber\":\"Dx001\","
            + "\"dxExchange\":\"xxxxxx\" }";

    private DxAddressResponse dxAddress;

    @Before
    public void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        dxAddress = objectMapper.readValue(json, DxAddressResponse.class);
    }

    @Test
    public void shouldPopulateData() {
        assertThat(dxAddress.getDxNumber(), is("Dx001"));
        assertThat(dxAddress.getDxExchange(), is("xxxxxx"));
    }
}