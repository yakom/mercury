package yakom.mercury;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/*
    required for the security filter to be registered.
    produces no DEBUG-level log output.
 */
public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {}
