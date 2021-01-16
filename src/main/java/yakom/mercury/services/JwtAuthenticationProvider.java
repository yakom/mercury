package yakom.mercury.services;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import yakom.mercury.model.IncomingJwtAuthentication;
import yakom.mercury.model.UserDetailsAuthentication;

import static org.slf4j.LoggerFactory.getLogger;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final Logger LOGGER = getLogger(JwtAuthenticationProvider.class);

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    public JwtAuthenticationProvider(
            JwtService jwtService,
            UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        var jwtAuthentication = (IncomingJwtAuthentication) authentication;

        String username;
        try {
            username = jwtService.validate(
                    jwtAuthentication.jwt, jwtAuthentication.requestType);
        } catch (BadCredentialsException e) {
            LOGGER.debug("Credentials invalid.", e);
            throw e;
        } catch (ExpiredJwtException e) {
            LOGGER.debug("Credentials expired.", e);
            throw new CredentialsExpiredException("JWT token expired.");
        } catch (Exception e) {
            LOGGER.debug("JWT token validation failed.", e);
            throw new BadCredentialsException("JWT token validation failed.");
        }

        var principal = userDetailsService.loadUserByUsername(username);

        return new UserDetailsAuthentication(
                principal, jwtAuthentication.requestType.getRole());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return IncomingJwtAuthentication.class
                .isAssignableFrom(authentication);
    }
}
