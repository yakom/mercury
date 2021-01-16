package yakom.mercury;

import com.fasterxml.jackson.core.JsonProcessingException;
import yakom.mercury.model.AuthenticationRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static yakom.mercury.Constants.JWT_HEADER;
import static yakom.mercury.Constants.WS_AUTHENTICATION_PATH;
import static yakom.mercury.MainContextConfiguration.OBJECT_MAPPER;
import static yakom.mercury.TestConstants.AUTHENTICATION_REQUEST;
import static yakom.mercury.TestConstants.HTTP_CLIENT;
import static yakom.mercury.TestConstants.MERCURY_URL;

public class TestUtilities {

    public static String serialize(AuthenticationRequest x) {
        try {
            return OBJECT_MAPPER.writeValueAsString(x);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpRequest newWSAuthenticationHTTPRequest(
            AuthenticationRequest x) {

        return HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(serialize(x)))
                .uri(URI.create(MERCURY_URL + WS_AUTHENTICATION_PATH))
                .headers("Content-Type", APPLICATION_JSON_VALUE)
                .build();
    }

    public static String generateNewWSJWT() throws IOException, InterruptedException {

        var authRequest = newWSAuthenticationHTTPRequest(AUTHENTICATION_REQUEST);

        var authResponse = HTTP_CLIENT.send(authRequest, BodyHandlers.discarding());
        assertEquals(SC_OK, authResponse.statusCode());

        var jwtOpt = authResponse.headers().firstValue(JWT_HEADER);
        assertTrue(jwtOpt.isPresent());

        return jwtOpt.get();
    }

    public static HttpRequest newWSHTTPRequest(String path, String jwt) {

        return HttpRequest
                .newBuilder().GET()
                .uri(URI.create(MERCURY_URL + path))
                .headers(
                        "Content-Type", APPLICATION_JSON_VALUE,
                        JWT_HEADER, jwt)
                .build();
    }

    public static HttpRequest newUIHTTPRequest(String path, String jwt) {

        return HttpRequest
                .newBuilder().GET()
                .uri(URI.create(MERCURY_URL + path))
                .headers(
                        "Cookie", "MERCURY_TOKEN=" + jwt,
                        "Content-Type", APPLICATION_JSON_VALUE)
                .build();
    }

    public static String deserializeMe(String response) {
        try {
            return OBJECT_MAPPER
                    .readerForMapOf(String.class)
                    .<Map<String, String>>readValue(response)
                    .get("username");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
