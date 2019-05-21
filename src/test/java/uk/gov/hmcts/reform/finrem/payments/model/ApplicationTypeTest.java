package uk.gov.hmcts.reform.finrem.payments.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.reform.finrem.payments.model.ApplicationType.CONSENTED;
import static uk.gov.hmcts.reform.finrem.payments.model.ApplicationType.CONTESTED;

public class ApplicationTypeTest {

    @Test
    public void checkAllApplicationTypes() {
        assertEquals("consented", CONSENTED.toString());
        assertEquals("contested", CONTESTED.toString());
    }


    @Test
    public void covertAllApplicationTypes() {
        assertEquals(ApplicationType.from("consented"), CONSENTED);
        assertEquals(ApplicationType.from("consented"), CONSENTED);

        assertEquals(ApplicationType.from("CONSENTED"), CONSENTED);
        assertEquals(ApplicationType.from("CONTESTED"), CONTESTED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwIllegalArumentException() {
        ApplicationType.from("abcd");
    }

}