package uk.gov.hmcts.reform.finrem.payments;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.FeeRequest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentRequest;
import uk.gov.hmcts.reform.finrem.payments.service.FeatureToggleService;

import java.math.BigDecimal;
import java.util.Collections;

public abstract class BaseContractTest extends BaseTest {

    @Autowired
    FeatureToggleService featureToggleService;

    protected PaymentRequest getPaymentRequest(BigDecimal amount) {
        PaymentRequest expectedRequest = new PaymentRequest();
        expectedRequest.setService("FINREM");
        expectedRequest.setCurrency("GBP");
        expectedRequest.setAmount(amount);
        expectedRequest.setCcdCaseNumber("test.case.id");
        if (featureToggleService.isPBAUsingCaseTypeEnabled()) {
            expectedRequest.setCaseType("FinancialRemedyMVP2");
        } else {
            expectedRequest.setSiteId("AA09");
        }
        expectedRequest.setAccountNumber("test.account");
        expectedRequest.setOrganisationName("test.organisation");
        expectedRequest.setCustomerReference("test.customer.reference");
        expectedRequest.setDescription("Financial Remedy Payment");

        FeeRequest feeRequest = new FeeRequest();
        feeRequest.setCode("test");
        feeRequest.setVersion("v1");
        feeRequest.setCalculatedAmount(amount);
        feeRequest.setVolume(1);

        expectedRequest.setFeesList(Collections.singletonList(feeRequest));
        return expectedRequest;
    }

}
