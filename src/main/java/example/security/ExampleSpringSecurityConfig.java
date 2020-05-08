/*
 * Copyright 2020, Verizon Media.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */

package example.security;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.session.ConcurrentSessionFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.GenericFilterBean;

import example.SecurityConfigProperties;

/**
 * Example setup for overriding the principal in security context to support elide-async.
 * You can remove this class if not using Async.
 */
@EnableWebSecurity
public class ExampleSpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .addFilterAfter(new DemoAuthFilter(), ConcurrentSessionFilter.class) // use demo auth filter
            .cors()
                .and()
            .headers().frameOptions().sameOrigin()  //Needed for Swagger and Graphiql iFrames.
                .and()
            .headers().cacheControl().disable()
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
    
    /**
     * Example Auth filter implementation for Elide Spring Boot Example to use async query.
     * This implementation fakes a Test user.
     * Please replace with your implementation when using this class.
     */
    class DemoAuthFilter extends GenericFilterBean {
        
        @Override
        public void doFilter(
          ServletRequest request, 
          ServletResponse response,
          FilterChain chain) throws IOException, ServletException {
            
            ElideSpringUser user = new ElideSpringUser("test", "{noop}test", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user, user.getPassword(), new ArrayList<>());

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authRequest);
            
            chain.doFilter(request, response);
        }

        /**
         * Example User.
         */
        class ElideSpringUser extends org.springframework.security.core.userdetails.User {

            private static final long serialVersionUID = 1L;
            private String name;

            public ElideSpringUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
                super(username, password,  authorities);
                this.name = username;
            }

            public String getName() {
                return name;
            }
        }
    }
}