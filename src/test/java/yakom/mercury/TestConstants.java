package yakom.mercury;

import yakom.mercury.model.AuthenticationRequest;

import java.net.http.HttpClient;
import java.time.Duration;

import static java.lang.System.getProperty;
import static java.net.http.HttpClient.Redirect.NEVER;
import static yakom.mercury.Constants.PASSWORD;
import static yakom.mercury.Constants.USERNAME;

public class TestConstants {

    static final String MERCURY_PROTOCOL = getProperty(
            "mercury.test.protocol", "http");
    static final String MERCURY_SERVICE = getProperty(
            "mercury.test.service", "localhost:9001");
    static final String MERCURY_URL = String.format(
            "%s://%s", MERCURY_PROTOCOL, MERCURY_SERVICE);

    static final HttpClient HTTP_CLIENT = HttpClient
            .newBuilder()
            .followRedirects(NEVER)
            .connectTimeout(Duration.ofSeconds(4L))
            .build();

    static final AuthenticationRequest AUTHENTICATION_REQUEST =
            new AuthenticationRequest(USERNAME, PASSWORD);
}
