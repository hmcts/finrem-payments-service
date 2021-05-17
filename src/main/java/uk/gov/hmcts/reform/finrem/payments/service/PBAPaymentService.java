package uk.gov.hmcts.reform.finrem.payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.finrem.payments.config.PBAPaymentServiceConfiguration;
import uk.gov.hmcts.reform.finrem.payments.error.InvalidTokenException;
import uk.gov.hmcts.reform.finrem.payments.error.PaymentException;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentRequest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentResponse;

import java.net.URI;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
public class PBAPaymentService {
    private final PBAPaymentServiceConfiguration serviceConfig;
    private final RestTemplate restTemplate;
    private final AuthTokenGenerator authTokenGenerator;

    public PaymentResponse makePayment(String authToken, PaymentRequest paymentRequest) {
        HttpEntity<PaymentRequest> request = buildPaymentRequest(authToken, paymentRequest);
        URI uri = buildUri();
        log.info("Inside makePayment, payment API uri : {}, request : {} ", uri, request);
        try {
            ResponseEntity<PaymentResponse> response = restTemplate.postForEntity(uri, request, PaymentResponse.class);
            log.info("Payment response: {} ", response);
            return response.getBody();
        } catch (Exception ex) {
            throw new PaymentException(ex);
        }
    }

    private HttpEntity<PaymentRequest> buildPaymentRequest(String authToken, PaymentRequest paymentRequest) {
        HttpHeaders headers = new HttpHeaders();
        if (!authToken.matches("^Bearer .+")) {
            throw new InvalidTokenException("Invalid user token");
        }
        headers.add("Authorization", authToken);
        headers.add("ServiceAuthorization", authTokenGenerator.generate());
        headers.add("Content-Type", "application/json");
        return new HttpEntity<>(paymentRequest, headers);
    }

    private URI buildUri() {
        return fromHttpUrl(serviceConfig.getUrl() + serviceConfig.getApi()).build().toUri();
    }
}
