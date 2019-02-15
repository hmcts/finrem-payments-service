package uk.gov.hmcts.reform.finrem.functional;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGeneratorFactory;


@Slf4j
@Configuration
@ComponentScan("uk.gov.hmcts.reform.finrem.functional")
@PropertySource("classpath:application.properties")
@PropertySource("classpath:application-aat.properties")
@EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
public class TestContextConfiguration {

    @Bean
    public AuthTokenGenerator serviceAuthTokenGenerator(@Value("${idam.auth.secret}")
                                                                   String secret,
                                                        @Value("${idam.auth.microservice}")
                                                                       String microService,
                                                        final ServiceAuthorisationApi serviceAuthorisationApi) {
        log.info("service.name: {}", microService);
        log.info(": {idam.s2s-auth.secret}", secret);
        return AuthTokenGeneratorFactory.createDefaultGenerator(secret, microService, serviceAuthorisationApi);
    }
}
