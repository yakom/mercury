package yakom.mercury.services;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static java.util.Collections.singleton;
import static yakom.mercury.Constants.PASSWORD;
import static yakom.mercury.Constants.USERNAME;
import static yakom.mercury.Constants.USER_ROLE;

public class DefaultUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        if (!USERNAME.equals(username))
            throw new UsernameNotFoundException(
                    "user " + username + " not found.");
        return new User(
                USERNAME, PASSWORD, singleton(
                new SimpleGrantedAuthority(USER_ROLE)));
    }
}
