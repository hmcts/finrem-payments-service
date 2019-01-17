package uk.gov.hmcts.reform.finrem.payments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import feign.FeignException;
import feign.Response;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.FeeRequest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentRequest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentResponse;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentStatusHistory;

import java.util.Collections;

public class SetUpUtils {

    public static  final int STATUS_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
    public static final String ACCOUNT_NUMBER = "Account12345";
    public static final String PAYMENT_STATUS = "Success";
    public static final String PAYMENT_REF = "RC-12345-2323-0712321320-23221";

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static FeignException feignError() {
        Response response = Response.builder().status(STATUS_CODE).headers(ImmutableMap.of()).build();
        return FeignException.errorStatus("test", response);
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
        try {
            return objectMapper.writeValueAsString(paymentRequest());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
        try {
            return objectMapper.writeValueAsString(paymentResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static PaymentStatusHistory paymentStatusHistory() {
        return PaymentStatusHistory.builder().errorCode("ERR").errorMessage("error").status("S").build();
    }
}
