package uk.gov.hmcts.reform.finrem.payments.smoketests;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SmokeTestConfiguration.class})
public class PaymentSmokeTests {

    @Value("${fees.url}")
    private String feeUrl;

    @Value("${fees.api}")
    private String feesApi;

    @Value("${fees.jurisdiction1}")
    private String jurisdiction1;

    @Value("${fees.jurisdiction2}")
    private String jurisdiction2;

    @Value("${fees.channel}")
    private String channel;

    @Value("${fees.service}")
    private String service;

    @Value("${fees.consented-event}")
    private String consentedEvent;

    @Value("${fees.consented-keyword}")
    private String consentedKeyword;

    @Value("${fees.contested-event}")
    private String contestedEvent;

    @Value("${fees.contested-keyword}")
    private String contestedKeyword;

    @Value("${http.timeout}")
    private int connectionTimeOut;

    @Value("${http.requestTimeout}")
    private int socketTimeOut;

    @Value("${http.readTimeout}")
    private int connectionManagerTimeOut;


    private RestAssuredConfig config;

    @Before
    public void setUp() {
        RestAssured.useRelaxedHTTPSValidation();
        config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", connectionTimeOut)
                        .setParam("http.socket.timeout", socketTimeOut)
                        .setParam("http.connection-manager.timeout", connectionManagerTimeOut));
    }

    @Test
    public void shouldGetConsentedFeeLookup() {
        given().config(config)
                .when()
                .queryParam("application-type", "consented")
                .get(feeUrl + feesApi)
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value());
    }


    @Test
    public void shouldGetContestedFeeLookup() {
        given().config(config)
                .when()
                .queryParam("application-type", "contested")
                .get(feeUrl + feesApi)
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}
