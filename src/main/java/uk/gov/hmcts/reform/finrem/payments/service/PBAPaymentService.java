package uk.gov.hmcts.reform.finrem.payments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.finrem.payments.config.PBAPaymentServiceConfiguration;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentRequest;
import uk.gov.hmcts.reform.finrem.payments.model.pba.payment.PaymentResponse;

import java.io.IOException;
import java.net.URI;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
public class PBAPaymentService {
    private final PBAPaymentServiceConfiguration serviceConfig;
    private final RestTemplate restTemplate;
    private final AuthTokenGenerator authTokenGenerator;
    private static final ObjectMapper mapper = new ObjectMapper();

    public PaymentResponse makePayment(String authToken, PaymentRequest paymentRequest) {
        HttpEntity<PaymentRequest> request = buildPaymentRequest(authToken, paymentRequest);
        URI uri = buildUri();
        log.info("Inside makePayment, PRD API uri : {}, request : {} ", uri, request);
        try {
            ResponseEntity<PaymentResponse> responseEntity = restTemplate.postForEntity(uri, request,
                    PaymentResponse.class);
            log.info("Payment response: {} ", responseEntity);
            return responseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            log.info("Payment error, exception : {} ", ex);
            return sendError(ex);
        }
    }

    private PaymentResponse sendError(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), PaymentResponse.class);
        } catch (IOException e) {
            log.error("payment-error-conversion exception : {} ", ex);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private HttpEntity<PaymentRequest> buildPaymentRequest(String authToken, PaymentRequest paymentRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authToken);
        headers.add("ServiceAuthorization", authTokenGenerator.generate());
        headers.add("Content-Type", "application/json");
        return new HttpEntity<>(paymentRequest, headers);
    }

    private URI buildUri() {
        return UriComponentsBuilder.fromHttpUrl(serviceConfig.getUrl() + serviceConfig.getApi()).build().toUri();
    }
}
