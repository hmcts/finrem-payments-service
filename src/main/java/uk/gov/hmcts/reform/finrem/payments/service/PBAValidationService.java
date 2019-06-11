package uk.gov.hmcts.reform.finrem.payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.finrem.payments.config.PBAValidationServiceConfiguration;
import uk.gov.hmcts.reform.finrem.payments.model.pba.validation.PBAAccount;
import uk.gov.hmcts.reform.finrem.payments.model.pba.validation.PBAValidationResponse;

import java.net.URI;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;


@Service
@RequiredArgsConstructor
@Slf4j
public class PBAValidationService {
    private final IdamService idamService;
    private final PBAValidationServiceConfiguration serviceConfig;
    private final RestTemplate restTemplate;

    public PBAValidationResponse isPBAValid(String authToken, String pbaNumber) {
        String emailId = idamService.getUserEmailId(authToken);
        URI uri = buildUri(emailId);
        log.info("Inside isPBAValid, PRD API uri : {}, emailId : {}", uri, emailId);
        try {
            HttpEntity request = buildRequest();
            ResponseEntity<PBAAccount> responseEntity = restTemplate.exchange(uri, GET, request, PBAAccount.class);
            PBAAccount pbaAccount = responseEntity.getBody();
            log.info("pbaAccount : {}", pbaAccount);
            boolean isValid = pbaAccount.getAccountList().contains(pbaNumber);
            return PBAValidationResponse.builder().pbaNumberValid(isValid).build();
        } catch (HttpClientErrorException ex) {
            return PBAValidationResponse.builder().build();
        }
    }

    private HttpEntity buildRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new HttpEntity<>(headers);
    }


    private URI buildUri(String emailId) {
        if (serviceConfig.isEnableOldUrl()) {
            return fromHttpUrl(serviceConfig.getOldUrl() + serviceConfig.getApi() + emailId).build().toUri();
        } else {
            return fromHttpUrl(serviceConfig.getUrl() + serviceConfig.getApi() + emailId).build().toUri();
        }
    }
}
