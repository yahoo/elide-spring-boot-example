/*
 * Copyright 2020, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */

package example.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import example.security.AppUserGrantedAuthority;

/**
 * Security configuration.
 * <p>
 * @see AppSecurityProperties
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(AppSecurityProperties.class)
public class SecurityConfiguration {
    /**
     * Default configuration where security is disabled.
     */
    @Configuration
    @ConditionalOnProperty(name = "app.security.enabled", havingValue = "false", matchIfMissing = true)
    static class DisabledSecurityConfiguration {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.cors(withDefaults())
                    .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                    .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests.anyRequest().permitAll())
                    .csrf(csrf -> csrf.disable());
            return http.build();
        }
    }

    /**
     * Configuration where security is enabled.
     */
    @Configuration
    @ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = false)
    static class EnabledSecurityConfiguration {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.formLogin(withDefaults())
                    .cors(withDefaults())
                    .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                    .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests.anyRequest().authenticated())
                    .csrf(this::configureCsrf);
            return http.build();
        }

        @Bean 
        PasswordEncoder passwordEncoder() { 
            return new BCryptPasswordEncoder(); 
        }

        @Bean
        UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
            UserDetails admin = User.withUsername("admin")
                    .password(passwordEncoder.encode("adminpass"))
                    .roles("ADMIN", "USER")
                    .authorities(new AppUserGrantedAuthority(1L))
                    .build();
            UserDetails user = User.withUsername("user")
                    .password(passwordEncoder.encode("userpass"))
                    .roles("USER")
                    .authorities(new AppUserGrantedAuthority(2L))
                    .build();
            return new InMemoryUserDetailsManager(admin, user);
        }

        /**
         * Configures CSRF protection using the Cookie to Header mechanism where a
         * XSRF-TOKEN cookie is set for the client to return by setting the X-XSRF-TOKEN
         * header. This protection requires Cross-origin resource sharing to be properly
         * set.
         * <p>
         * CSRF protection is required for browser clients where the browser
         * automatically sends credentials along with requests such as cookies eg.
         * JSESSIONID or Basic Authentication credentials requested via
         * WWW-Authenticate.
         *
         * @param csrf the configurer
         * @see <a href=
         *      "https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html#employing-custom-request-headers-for-ajaxapi">Employing
         *      Custom Request Headers for AJAX/API</>
         * @see <a href=
         *      "https://en.wikipedia.org/wiki/Cross-site_request_forgery#Cookie-to-header_token">Cookie-to-header
         *      token</a>
         */
        void configureCsrf(CsrfConfigurer<HttpSecurity> csrf) {
            CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
            csrfTokenRequestAttributeHandler.setCsrfRequestAttributeName(null); // turn off deferred loading
            csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler);
        }
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(AppSecurityProperties properties) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(properties.getOrigin()));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setMaxAge(Duration.ofHours(1));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); 
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
