package uk.gov.hmcts.reform.finrem.payments.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import uk.gov.hmcts.reform.finrem.payments.PaymentsApplication;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements WebMvcConfigurer {

    @Value("${documentation.swagger.enabled}")
    private boolean swaggerEnabled;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(PaymentsApplication.class.getPackage().getName()))
                .build()
                .useDefaultResponseMessages(true)
                .apiInfo(apiInfo()).enable(swaggerEnabled);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Financial Remedy payments Service")
                .description("Given a case data, This service will provide the financial remedy payment features "
                        + "like fee lookUp,pba-validate and pba-payment")
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        if (swaggerEnabled) {
            registry.addResourceHandler("/swagger-ui.html**")
                    .addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
    }

}
