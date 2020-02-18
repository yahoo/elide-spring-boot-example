package example.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Example setup for overriding the principal in security context to support elide-async.
 * Remove this class if not using Async
 */
@Configuration
@EnableWebSecurity
public class ElideSpringSecurityContextConfig extends WebSecurityConfigurerAdapter {

    private static final String TEST_USER = "test";
    private static final String TEST_PASSWORD = "{noop}test"; //noop for plain-text
    private static final String TEST_ROLE = "ROLE_ADMIN";

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth.
            inMemoryAuthentication()
            .withUser(TEST_USER).password(TEST_PASSWORD)
            .authorities(TEST_ROLE);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.
             authorizeRequests()
            .antMatchers("/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .httpBasic()
            .and()
            .headers().frameOptions().disable()
            .and()
            .csrf().disable();
    }
}
