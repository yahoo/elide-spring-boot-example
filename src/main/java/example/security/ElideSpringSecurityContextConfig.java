package example.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.ConcurrentSessionFilter;

/**
 * Example setup for overriding the principal in security context to support elide-async.
 * Remove this class if not using Async
 */
@EnableWebSecurity
public class ElideSpringSecurityContextConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        http
            .addFilterAfter(new CustomAuthFilter(), ConcurrentSessionFilter.class)
            .requestMatchers()
                .antMatchers("/**")
            .and()
            .authorizeRequests()
                .anyRequest().authenticated()
            .and()
            .formLogin().disable()
            .httpBasic()
            .and()
            .headers().frameOptions().disable()
            .and()
            .csrf().disable();
    }
}