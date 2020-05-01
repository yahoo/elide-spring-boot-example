package example.security;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.ConcurrentSessionFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import example.SecurityConfigProperties;

/**
 * Example setup for overriding the principal in security context to support elide-async.
 * Remove this class if not using Async
 */
//@Configuration
//@EnableConfigurationProperties(SecurityConfigProperties.class)
@EnableWebSecurity
public class ElideSpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .addFilterAfter(new CustomAuthFilter(), ConcurrentSessionFilter.class)
            .cors()
                .and()
            .headers().frameOptions().sameOrigin()  //Needed for Swagger and Graphiql iFrames.
                .and()
            .headers().cacheControl().disable()
                .and()
            .formLogin().disable().httpBasic()
                .and()
            .authorizeRequests().antMatchers("/").permitAll()
                .and()
            .csrf().disable();  //Allow for GraphQL POSTs
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(SecurityConfigProperties properties) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(properties.getOrigin()));
        configuration.setAllowedMethods(Arrays.asList("GET", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setMaxAge(Duration.ofHours(1));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}