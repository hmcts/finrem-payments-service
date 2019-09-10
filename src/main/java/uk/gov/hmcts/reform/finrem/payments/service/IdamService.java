package uk.gov.hmcts.reform.finrem.payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.finrem.payments.config.IdamServiceConfiguration;

import java.net.URI;
import java.util.Map;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;


@Service
@RequiredArgsConstructor
@Slf4j
public class IdamService {
    private final IdamServiceConfiguration serviceConfig;
    private final RestTemplate restTemplate;

    public String getUserEmailId(String authToken) {
        HttpEntity request = buildAuthRequest(authToken);
        URI uri = buildUri();
        log.info("Inside getUserEmailId, IDAM API uri : {}, request : {} ", uri, request);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, request, Map.class);
        Map result = responseEntity.getBody();
        return result.get("email").toString().toLowerCase();
    }

    private HttpEntity buildAuthRequest(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        if (authToken.matches("^Bearer .+")) {
            headers.add("Authorization", authToken);
        }
        headers.add("Content-Type", "application/json");
        return new HttpEntity<>(headers);
    }

    private URI buildUri() {
        return fromHttpUrl(serviceConfig.getUrl() + serviceConfig.getApi()).build().toUri();
    }
}
