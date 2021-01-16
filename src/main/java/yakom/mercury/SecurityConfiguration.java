package yakom.mercury;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.header.HeaderWriterFilter;
import yakom.mercury.services.AuthenticationFilter;
import yakom.mercury.services.DefaultUserDetailsService;
import yakom.mercury.services.JwtAuthenticationProvider;
import yakom.mercury.services.JwtFilter;
import yakom.mercury.services.JwtService;

import javax.servlet.Filter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public JwtService jwtService() {
        return new JwtService(
                environment.getProperty("JWT_SECRET"),
                environment.getProperty(
                        "JWT_VALIDITY_MINUTES", Long.class));
    }

    @Bean
    public Filter jwtFilter() throws Exception {
        return new JwtFilter(authenticationManager());
    }

    @Bean
    public Filter authenticationFilter() throws Exception {
        return new AuthenticationFilter(
                authenticationManager(),
                jwtService(), objectMapper);
    }

    @Bean
    public UserDetailsService defaultUserDetailsService() {
        return new DefaultUserDetailsService();
    }

    @Bean
    public AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(
                jwtService(), defaultUserDetailsService());
    }

    @Bean
    public AuthenticationProvider loginAuthenticationProvider() {
        var result = new DaoAuthenticationProvider();
        result.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        result.setUserDetailsService(defaultUserDetailsService());
        return result;
    }

    @Bean(name = "defaultAuthenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(loginAuthenticationProvider())
                .authenticationProvider(jwtAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .anonymous().disable()
                .csrf().disable()
                .exceptionHandling().disable()
                .httpBasic().disable()
                .logout().disable()
                .requestCache().disable()
                .securityContext().disable()
                .servletApi().disable()
                .sessionManagement().disable()
                .addFilterAfter(
                        authenticationFilter(),
                        HeaderWriterFilter.class)
                .addFilterAfter(
                        jwtFilter(),
                        AuthenticationFilter.class)
                .authorizeRequests()
                .anyRequest().authenticated();
    }
}
