package uk.gov.hmcts.reform.finrem.functional.util;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.reform.finrem.functional.idam.IdamUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


@Component
public class FunctionalTestUtils {

    @Value("${idam.api.url}")
    public String baseServiceOauth2Url = "";
    @Value("${user.id.url}")
    private String userId;
    @Value("${idam.username}")
    private String idamUserName;
    @Value("${idam.userpassword}")
    private String idamUserPassword;
    @Value("${idam.s2s-auth.microservice}")
    private String microservice;
    @Autowired
    private IdamUtils idamUtils;


    public String getJsonFromFile(String fileName) {
        try {
            File file = ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Headers getHeader() {
        String authToken = idamUtils.generateUserTokenWithNoRoles(idamUserName, idamUserPassword);
        System.out.println(authToken);
        return Headers.headers(

                new Header("Authorization", "Bearer "
                        + authToken));
    }

    public Headers getHeaders() {
        return Headers.headers(
            new Header("Authorization", "Bearer "
                + idamUtils.generateUserTokenWithNoRoles(idamUserName, idamUserPassword)),
            new Header("Content-Type", ContentType.JSON.toString()));
    }
}
