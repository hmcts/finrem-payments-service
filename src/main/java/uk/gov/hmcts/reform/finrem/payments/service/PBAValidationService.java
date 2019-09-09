package uk.gov.hmcts.reform.finrem.payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.finrem.payments.config.PBAValidationServiceConfiguration;
import uk.gov.hmcts.reform.finrem.payments.model.pba.validation.PBAOrganisationResponse;
import uk.gov.hmcts.reform.finrem.payments.model.pba.validation.PBAValidationResponse;

import java.net.URI;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;


@Service
@RequiredArgsConstructor
@Slf4j
@EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
public class PBAValidationService {
    private final IdamService idamService;
    private final PBAValidationServiceConfiguration serviceConfig;
    private final RestTemplate restTemplate;
    private final AuthTokenGenerator authTokenGenerator;

    public PBAValidationResponse isPBAValid(String authToken, String pbaNumber) {
        String emailId = idamService.getUserEmailId(authToken);
        URI uri = buildUri(emailId);
        log.info("Inside isPBAValid, PRD API uri : {}, emailId : {}", uri, emailId);
        try {
            HttpEntity request;
            if (serviceConfig.isEnableOldUrl()) {
                request = buildRequest();
            } else {
                request = buildRequest(authToken);
            }
            log.info("before prd call ...");
            ResponseEntity<PBAOrganisationResponse> responseEntity = restTemplate.exchange(uri, GET,
                    request, PBAOrganisationResponse.class);
            PBAOrganisationResponse pbaOrganisationResponse = responseEntity.getBody();
            log.info("pbaOrganisationEntityResponse : {}", pbaOrganisationResponse);
            boolean isValid = pbaOrganisationResponse.getOrganisationEntityResponse().getPaymentAccount()
                    .contains(pbaNumber);
            return PBAValidationResponse.builder().pbaNumberValid(isValid).build();
        } catch (HttpClientErrorException ex) {
            log.info("HttpClientErrorException ...", ex);
            return PBAValidationResponse.builder().build();
        }
    }

    private HttpEntity buildRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new HttpEntity<>(headers);
    }

    private HttpEntity buildRequest(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authToken);
        headers.add("Content-Type", "application/json");
        headers.add("ServiceAuthorization", authTokenGenerator.generate());
        return new HttpEntity<>(headers);
    }


    private URI buildUri(String emailId) {
        if (serviceConfig.isEnableOldUrl()) {
            return fromHttpUrl(serviceConfig.getOldUrl() + serviceConfig.getOldApi() + emailId).build().toUri();
        } else if (serviceConfig.isEnableLegacyUrl()) {
            return fromHttpUrl(serviceConfig.getUrl() + serviceConfig.getLegacyApi() + emailId).build().toUri();
        } else {
            return fromHttpUrl(serviceConfig.getUrl() + serviceConfig.getApi())
                    .queryParam("email", emailId)
                    .build().toUri();
        }
    }
}
