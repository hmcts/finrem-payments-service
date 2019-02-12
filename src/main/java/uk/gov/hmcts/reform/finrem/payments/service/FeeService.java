package uk.gov.hmcts.reform.finrem.payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.finrem.payments.config.FeeServiceConfiguration;
import uk.gov.hmcts.reform.finrem.payments.model.fee.FeeResponse;

import java.net.URI;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;


@Service
@RequiredArgsConstructor
@Slf4j
public class FeeService {
    private final FeeServiceConfiguration serviceConfig;
    private final RestTemplate restTemplate;

    public FeeResponse getApplicationFee() {
        URI uri = buildUri();
        log.info("Inside getApplicationFee, FeeResponse API uri : {} ", uri);
        ResponseEntity<FeeResponse> response = restTemplate.getForEntity(uri, FeeResponse.class);
        log.info("Fee response : {} ", response);
        return response.getBody();
    }

    private URI buildUri() {
        return fromHttpUrl(serviceConfig.getUrl() + serviceConfig.getApi())
                .queryParam("service", serviceConfig.getService())
                .queryParam("jurisdiction1", serviceConfig.getJurisdiction1())
                .queryParam("jurisdiction2", serviceConfig.getJurisdiction2())
                .queryParam("channel", serviceConfig.getChannel())
                .queryParam("event", serviceConfig.getEvent())
                .queryParam("keyword", serviceConfig.getKeyword())
                .build()
                .encode()
                .toUri();
    }
}
