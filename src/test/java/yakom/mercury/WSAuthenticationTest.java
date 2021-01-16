package yakom.mercury;

import yakom.mercury.model.AuthenticationRequest;

import java.io.IOException;
import java.net.http.HttpResponse.BodyHandlers;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static yakom.mercury.Constants.JWT_HEADER;
import static yakom.mercury.Constants.UI_ME_PATH;
import static yakom.mercury.Constants.USERNAME;
import static yakom.mercury.Constants.WS_ME_PATH;
import static yakom.mercury.TestConstants.HTTP_CLIENT;
import static yakom.mercury.TestUtilities.deserializeMe;
import static yakom.mercury.TestUtilities.generateNewWSJWT;
import static yakom.mercury.TestUtilities.newUIHTTPRequest;
import static yakom.mercury.TestUtilities.newWSAuthenticationHTTPRequest;
import static yakom.mercury.TestUtilities.newWSHTTPRequest;

public class WSAuthenticationTest {

    @SystemTest
    public void testInvalidCredentials() throws IOException, InterruptedException {

        var request = newWSAuthenticationHTTPRequest(new AuthenticationRequest("invalid", "invalid"));
        var response = HTTP_CLIENT.send(request, BodyHandlers.discarding());

        assertEquals(SC_UNAUTHORIZED, response.statusCode());
        assertTrue(response.headers().firstValue(JWT_HEADER).isEmpty());
    }

    /* a UI endpoint should reject a WS-type token. */
    @SystemTest
    public void testUIMe() throws IOException, InterruptedException {

        var meResponse = HTTP_CLIENT.send(
                newWSHTTPRequest(UI_ME_PATH, generateNewWSJWT()),
                BodyHandlers.ofString());

        assertEquals(SC_FORBIDDEN, meResponse.statusCode());
    }

    /* any UI-type request with a WS-type token should fail. */
    @SystemTest
    public void testUIRequest() throws IOException, InterruptedException {

        var response = HTTP_CLIENT.send(
                newUIHTTPRequest("/irrelevant", generateNewWSJWT()),
                BodyHandlers.ofString());

        assertEquals(SC_FORBIDDEN, response.statusCode());
    }

    @SystemTest
    public void testWSMe() throws IOException, InterruptedException {

        var meResponse = HTTP_CLIENT.send(
                newWSHTTPRequest(WS_ME_PATH, generateNewWSJWT()),
                BodyHandlers.ofString());

        assertEquals(SC_OK, meResponse.statusCode());
        assertEquals(USERNAME, deserializeMe(meResponse.body()));
    }
}
