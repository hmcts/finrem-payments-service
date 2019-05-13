package uk.gov.hmcts.reform.finrem.payments.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "fees")
public class FeeServiceConfiguration {
    private String url;
    private String api;
    private String channel;
    private String jurisdiction1;
    private String jurisdiction2;
    private String service;

    private String consentedEvent;
    private String consentedKeyword;

    private String contestedEvent;
    private String contestedKeyword;
}