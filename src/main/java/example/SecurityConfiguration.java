/*
 * Copyright 2020, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */

package example;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Disables security for testing.
 */
@Configuration
@EnableConfigurationProperties(SecurityConfigProperties.class)
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        final Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer =
                httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable();

        final Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>
                authorizeCustomizer = registry -> registry.requestMatchers("/**").permitAll();

        final Customizer<CorsConfigurer<HttpSecurity>> corsCustomizer = configurer -> {
            //hopefully the bean below gets placed rather than nothing.  This has a weird signature
        };

        final Customizer<HeadersConfigurer<HttpSecurity>.FrameOptionsConfig> frameOptionsConfig =
                config -> config.sameOrigin();

        final Customizer<HeadersConfigurer<HttpSecurity>> headerCustomizer = configurer -> {
            configurer.frameOptions(frameOptionsConfig);
        };

        http.cors(corsCustomizer)
                .headers(headerCustomizer)
                .authorizeHttpRequests(authorizeCustomizer)
                .csrf(csrfCustomizer)
                .headers(headerCustomizer);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(SecurityConfigProperties properties) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(properties.getOrigin()));  
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setMaxAge(Duration.ofHours(1));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); 
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
