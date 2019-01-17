package uk.gov.hmcts.reform.finrem.payments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import feign.FeignException;
import feign.Response;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.finrem.payments.model.fee.Fee;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.FeeRequest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentRequest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentResponse;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentStatusHistory;
import uk.gov.hmcts.reform.finrem.payments.model.pba.validation.PBAAccount;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class SetUpUtils {

    public static  final int STATUS_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
    public static final String ACCOUNT_NUMBER = "Account12345";
    public static final String PAYMENT_STATUS = "Success";
    public static final String PAYMENT_REF = "RC-12345-2323-0712321320-23221";

    public static final String FEE_CODE = "CODE";
    public static final String FEE_DESC = "Description";
    public static final BigDecimal FEE_AMOUNT = new BigDecimal("10");
    public static final String FEE_VERSION = "v1";

    public static final String PBA_NUMBER = "PBA";

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static FeignException feignError() {
        Response response = Response.builder().status(STATUS_CODE).headers(ImmutableMap.of()).build();
        return FeignException.errorStatus("test", response);
    }

    public static String pBAAccount() {
        PBAAccount pbaAccount = new PBAAccount();
        pbaAccount.setAccountList(ImmutableList.of(PBA_NUMBER));

        return objectToJson(pbaAccount);
    }

    public static Fee feeResponse() {
        Fee fee = new Fee();
        fee.setCode(FEE_CODE);
        fee.setDescription(FEE_DESC);
        fee.setFeeAmount(FEE_AMOUNT);
        fee.setVersion(FEE_VERSION);

        return fee;
    }

    public static String feeResponseString() {
        return objectToJson(feeResponse());
    }

    public static PaymentResponse paymentResponse() {
        return PaymentResponse.builder()
                .status(PAYMENT_STATUS)
                .reference(PAYMENT_REF)
                .statusHistories(ImmutableList.of()).build();
    }

    public static PaymentResponse paymentResponseClientError() {
        return PaymentResponse.builder()
                .status(PAYMENT_STATUS)
                .reference(PAYMENT_REF)
                .statusHistories(ImmutableList.of(paymentStatusHistory())).build();
    }

    public static String paymentResponseErrorToString() {
        return paymentResponseToString(paymentResponseClientError());
    }

    public static String paymentResponseToString() {
        return paymentResponseToString(paymentResponse());
    }

    public static String paymentRequestStringContent() {
        return objectToJson(paymentRequest());
    }

    public static PaymentRequest paymentRequest() {
        long amountToPay = Long.valueOf("12");
        FeeRequest fee = FeeRequest.builder()
                .calculatedAmount(amountToPay)
                .code("FEE0640")
                .version("v1")
                .build();
        return PaymentRequest.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .caseReference("ED12345")
                .customerReference("SOL1")
                .organisationNname("ORG SOL1")
                .amount(amountToPay)
                .feesList(Collections.singletonList(fee))
                .build();
    }

    private static String paymentResponseToString(PaymentResponse paymentResponse) {
        return objectToJson(paymentResponse);
    }

    private static PaymentStatusHistory paymentStatusHistory() {
        return PaymentStatusHistory.builder().errorCode("ERR").errorMessage("error").status("S").build();
    }

    private static String objectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
