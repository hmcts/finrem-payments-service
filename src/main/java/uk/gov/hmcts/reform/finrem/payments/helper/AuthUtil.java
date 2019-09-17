package uk.gov.hmcts.reform.finrem.payments.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.finrem.payments.error.InvalidTokenException;

@SuppressWarnings("squid:S1118")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthUtil {

    private static final String BEARER = "Bearer ";

    public static String getBearerToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new InvalidTokenException("Invalid Token");
        }

        return token.startsWith(BEARER) ? token : BEARER.concat(token);
    }
}