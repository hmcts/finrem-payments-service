package uk.gov.hmcts.reform.finrem.payments.service;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.finrem.payments.BaseServiceTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(Enclosed.class)
public class FeatureToggleServiceTest {

    @RunWith(SpringRunner.class)
    @SpringBootTest(properties = {
            "feature.toggle.pba_case_type=true"
    })
    public static class PbaToggleSwitchedOn extends BaseServiceTest {

        @Autowired
        private FeatureToggleService featureToggleService;

        @Test
        public void isPbaToggleEnabledReturnsTrue() {
            assertThat(featureToggleService.isPBAUsingCaseTypeEnabled(), is(true));
        }
    }

    @RunWith(SpringRunner.class)
    @SpringBootTest(properties = {
            "feature.toggle.pba_case_type=false"
    })
    public static class PbaToggleSwitchedOff extends BaseServiceTest {

        @Autowired
        private FeatureToggleService featureToggleService;

        @Test
        public void isPbaToggleEnabledReturnsFalse() {
            assertThat(featureToggleService.isPBAUsingCaseTypeEnabled(), is(false));
        }
    }
}
