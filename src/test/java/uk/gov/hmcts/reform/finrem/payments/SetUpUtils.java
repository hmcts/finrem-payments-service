package uk.gov.hmcts.reform.finrem.payments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import feign.FeignException;
import feign.Response;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.finrem.payments.model.fee.FeeResponse;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.FeeRequest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentRequest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentResponse;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentStatusHistory;
import uk.gov.hmcts.reform.finrem.payments.model.pba.validation.PBAAccount;

import java.math.BigDecimal;
import java.util.Collections;

public class SetUpUtils {

    public static  final int STATUS_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
    public static final String ACCOUNT_NUMBER = "Account12345";
    public static final String SITE_ID = "AA03";
    public static final String PAYMENT_SUCCESS_STATUS = "Success";
    public static final String PAYMENT_FAILED_STATUS = "Failed";
    public static final String PAYMENT_REF = "RC-12345-2323-0712321320-23221";

    public static final String FEE_CODE = "CODE";
    public static final String FEE_DESC = "Description";
    public static final BigDecimal FEE_AMOUNT = BigDecimal.TEN;
    public static final String FEE_VERSION = "v1";

    public static final String PBA_NUMBER = "PBA";

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static FeignException feignError() {
        Response response = Response.builder().status(STATUS_CODE).headers(ImmutableMap.of()).build();
        return FeignException.errorStatus("test", response);
    }

    public static String pbaAccount() {
        PBAAccount pbaAccount = PBAAccount.builder().accountList(ImmutableList.of(PBA_NUMBER)).build();
        return objectToJson(pbaAccount);
    }

    public static FeeResponse feeResponse() {
        FeeResponse feeResponse = new FeeResponse();
        feeResponse.setCode(FEE_CODE);
        feeResponse.setDescription(FEE_DESC);
        feeResponse.setFeeAmount(FEE_AMOUNT);
        feeResponse.setVersion(FEE_VERSION);
        return feeResponse;
    }

    public static String feeResponseString() {
        return objectToJson(feeResponse());
    }

    public static PaymentResponse paymentResponse() {
        return PaymentResponse.builder()
                .status(PAYMENT_SUCCESS_STATUS)
                .reference(PAYMENT_REF)
                .statusHistories(ImmutableList.of()).build();
    }

    public static PaymentResponse paymentResponseClient404Error() {
        return PaymentResponse.builder()
                .error(HttpStatus.NOT_FOUND.toString())
                .message("Account information could not be found")
                .build();
    }

    public static PaymentResponse paymentResponseClient401Error() {
        return PaymentResponse.builder()
                .status(PAYMENT_FAILED_STATUS)
                .reference(PAYMENT_REF)
                .statusHistories(ImmutableList.of(paymentStatusHistory())).build();
    }

    public static String paymentResponseErrorToString() {
        return objectToJson(paymentResponseClient401Error());
    }

    public static String paymentResponseToString() {
        return objectToJson(paymentResponse());
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
                .siteId(ACCOUNT_NUMBER)
                .caseReference("ED12345")
                .customerReference("SOL1")
                .organisationName("ORG SOL1")
                .amount(amountToPay)
                .feesList(Collections.singletonList(fee))
                .build();
    }

    private static PaymentStatusHistory paymentStatusHistory() {
        return PaymentStatusHistory.builder().errorCode("ERR").errorMessage("error").status("S").build();
    }

    private static String objectToJson(Object object) {
        try {
            String value = objectMapper.writeValueAsString(object);
            System.out.println(" value = [" + value + "]");
            return value;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
