package yakom.mercury.model;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static java.util.Collections.emptySet;

public class IncomingJwtAuthentication implements Authentication {

    public final String jwt;
    public final RequestType requestType;

    public IncomingJwtAuthentication(
            @NonNull String jwt, @NonNull RequestType requestType) {
        this.jwt = jwt;
        this.requestType = requestType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return emptySet();
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return jwt;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {}

    @Override
    public String getName() {
        return jwt;
    }
}
