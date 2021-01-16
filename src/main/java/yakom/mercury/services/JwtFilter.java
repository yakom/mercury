package yakom.mercury.services;

import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import yakom.mercury.model.IncomingJwtAuthentication;
import yakom.mercury.model.RequestType;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.security.core.context.SecurityContextHolder.createEmptyContext;
import static yakom.mercury.Constants.JWT_HEADER;
import static yakom.mercury.model.RequestType.UI;
import static yakom.mercury.model.RequestType.WS;
import static yakom.mercury.services.AuthenticationFilter.MERCURY_COOKIE_NAME;

/*
    this filter authenticates requests based on the required JWT.
    it has to be placed in the Spring security chain after the
    AuthenticationFilter.
 */
public class JwtFilter extends GenericFilterBean {

    private final Logger LOGGER = getLogger(JwtFilter.class);

    private final AuthenticationManager authenticationManager;

    public JwtFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private static class JWTData {

        public final String jwt;
        public final RequestType requestType;

        public JWTData(String jwt, RequestType requestType) {
            this.jwt = jwt;
            this.requestType = requestType;
        }
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain) {

        var request = (HttpServletRequest) servletRequest;
        var response = (HttpServletResponse) servletResponse;

        try {
            JWTData jwtData = extract(request);
            if (null == jwtData) {
                response.setStatus(SC_UNAUTHORIZED);
                return;
            }

            Authentication result;
            try {
                result = authenticationManager.authenticate(
                        new IncomingJwtAuthentication(
                                jwtData.jwt, jwtData.requestType));
            } catch (AuthenticationException e) {
                response.setStatus(SC_FORBIDDEN);
                return;
            }

            var context = createEmptyContext();
            context.setAuthentication(result);
            SecurityContextHolder.setContext(context);

            filterChain.doFilter(servletRequest, servletResponse);

        } catch (Exception e) {
            LOGGER.error("Request authentication failed.", e);
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static JWTData extract(HttpServletRequest request) {

        String jwt = request.getHeader(JWT_HEADER);
        if (null != jwt)
            return new JWTData(jwt, WS);

        Cookie[] cookies = request.getCookies();
        if (null == cookies)
            return null;

        for (Cookie cookie : cookies)
            if (MERCURY_COOKIE_NAME.equalsIgnoreCase(cookie.getName()))
                return new JWTData(cookie.getValue(), UI);

        return null;
    }
}
