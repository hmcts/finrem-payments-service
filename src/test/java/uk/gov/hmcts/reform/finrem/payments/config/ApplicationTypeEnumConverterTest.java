package uk.gov.hmcts.reform.finrem.payments.config;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.finrem.payments.model.ApplicationType.CONSENTED;
import static uk.gov.hmcts.reform.finrem.payments.model.ApplicationType.CONTESTED;

public class ApplicationTypeEnumConverterTest {

    @Test
    public void shouldConvertFromStringConsentedToEnumConsented() {
        ApplicationTypeEnumConverter converter = new ApplicationTypeEnumConverter();
        converter.setAsText("consented");
        assertThat(converter.getValue()).isEqualTo(CONSENTED);
    }

    @Test
    public void shouldConvertFromStringContestedToEnumContested() {
        ApplicationTypeEnumConverter converter = new ApplicationTypeEnumConverter();
        converter.setAsText("contested");
        assertThat(converter.getValue()).isEqualTo(CONTESTED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfIncorrectEnumValueIsProvided() {
        ApplicationTypeEnumConverter converter = new ApplicationTypeEnumConverter();
        converter.setAsText("abc");
    }

}