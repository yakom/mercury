package yakom.mercury.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.GenericFilterBean;
import yakom.mercury.model.AuthenticationRequest;
import yakom.mercury.model.RequestType;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static yakom.mercury.Constants.JWT_HEADER;
import static yakom.mercury.Constants.UI_AUTHENTICATION_PATH;
import static yakom.mercury.Constants.WS_AUTHENTICATION_PATH;

/*
    this filter is designed to respond only to authentication requests
    and issue a JWT after validating provided credentials.
 */
public class AuthenticationFilter extends GenericFilterBean {

    static final String MERCURY_COOKIE_NAME = "MERCURY_TOKEN";

    private final Logger LOGGER = getLogger(AuthenticationFilter.class);

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final ObjectMapper objectMapper;

    public AuthenticationFilter(
            AuthenticationManager authenticationManager,
            JwtService jwtService, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {

        var request = (HttpServletRequest) servletRequest;
        var response = (HttpServletResponse) servletResponse;

        var requestType = getRequestType(request);
        if (null == requestType) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try {
            AuthenticationRequest authenticationRequest;
            try {
                authenticationRequest = objectMapper.readValue(
                        request.getInputStream(), AuthenticationRequest.class);
            } catch (Exception e) {
                response.setStatus(SC_BAD_REQUEST);
                return;
            }

            Authentication token = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.username, authenticationRequest.password);
            try {
                token = authenticationManager.authenticate(token);
            } catch (AuthenticationException e) {
                response.setStatus(SC_UNAUTHORIZED);
                return;
            }

            String jwt = jwtService.generate(token.getName(), requestType);

            if (RequestType.UI.equals(requestType)) {
                Cookie tokenCookie = new Cookie(MERCURY_COOKIE_NAME, jwt);
                tokenCookie.setHttpOnly(true);
                tokenCookie.setSecure(true);

                response.addCookie(tokenCookie);
            } else
                response.setHeader(JWT_HEADER, jwt);

            response.setStatus(SC_OK);

        } catch (Exception e) {
            LOGGER.error("Authentication request handling failed.", e);
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static RequestType getRequestType(HttpServletRequest request) {

        if (!(APPLICATION_JSON_VALUE.equalsIgnoreCase(request.getContentType())
              && POST.name().equalsIgnoreCase(request.getMethod())))
            return null;

        if (UI_AUTHENTICATION_PATH.equalsIgnoreCase(request.getServletPath()))
            return RequestType.UI;
        else if (WS_AUTHENTICATION_PATH.equalsIgnoreCase(request.getServletPath()))
            return RequestType.WS;

        return null;
    }
}
