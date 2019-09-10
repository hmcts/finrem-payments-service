package uk.gov.hmcts.reform.finrem.payments.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "pba.validation")
public class PBAValidationServiceConfiguration {
    private String url;
    private String api;
    @Value("${pba.validation.old.url}")
    private String oldUrl;
    @Value("${pba.validation.old.api}")
    private String oldApi;
    @Value("${pba.validation.old.url.enabled}")
    private boolean enableOldUrl;
    @Value("${pba.validation.legacy.api}")
    private String legacyApi;
    @Value("${pba.validation.old.url.enabled}")
    private boolean enableLegacyUrl;
}
