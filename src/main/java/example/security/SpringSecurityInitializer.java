package example.security;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * Spring magically finds this file and enables security.
 */
public class SpringSecurityInitializer extends AbstractSecurityWebApplicationInitializer {
    public SpringSecurityInitializer() {
        super(WebSecurityConfig.class);
    }
}
