package yakom.mercury.model;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

public class UserDetailsAuthentication implements Authentication {

    private final UserDetails principal;
    private final Collection<GrantedAuthority> authorities;

    public UserDetailsAuthentication(
            @NonNull UserDetails principal,
            @NonNull String additionalRole) {
        this.principal = principal;
        authorities = new HashSet<>(principal.getAuthorities().size() + 1, 1F);
        authorities.addAll(principal.getAuthorities());
        authorities.add(new SimpleGrantedAuthority(additionalRole));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return principal.getPassword();
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {}

    @Override
    public String getName() {
        return principal.getUsername();
    }
}
