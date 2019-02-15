package uk.gov.hmcts.reform.finrem.functional;


import feign.Feign;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;


@Slf4j
@Configuration
@ComponentScan("uk.gov.hmcts.reform.finrem.functional")
@EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
@PropertySource("classpath:application.properties")
@PropertySource("classpath:application-aat.properties")
public class TestContextConfiguration {

    @Bean
    public ServiceAuthTokenGenerator serviceAuthTokenGenerator(@Value("${idam.auth.secret}")
                                                                   String secret,
                                                               @Value("${idam.auth.microservice}")
                                                                       String microservice,
                                                               final ServiceAuthorisationApi serviceAuthorisationApi) {
        log.info("service.name: {}", microservice);
        log.info(": {idam.s2s-auth.secret}", secret);
        return new ServiceAuthTokenGenerator(secret, microservice, serviceAuthorisationApi);
    }
}
