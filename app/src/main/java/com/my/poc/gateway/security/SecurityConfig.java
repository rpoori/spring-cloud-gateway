package com.my.poc.gateway.security;

import com.my.poc.gateway.document.CustomDocumentAuthenticationWebFilter;
import com.my.poc.gateway.document.CustomDocumentServerAuthenticationConverter;
import com.my.poc.gateway.document.CustomDocumentServerAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.Arrays;

@EnableWebFluxSecurity
@Profile("!test")
public class SecurityConfig {

    @Autowired
    private CustomReactiveAuthenticationManager customReactiveAuthenticationManager;

    @Autowired
    private CustomSecurityContextRepository customSecurityContextRepository;

    @Autowired
    private CustomDocumentServerAuthenticationConverter customDocumentServerAuthenticationConverter;

    @Autowired
    private CustomDocumentServerAuthenticationSuccessHandler customDocumentServerAuthenticationSuccessHandler;

    private final ServerWebExchangeMatcher documentMatcher = new PathPatternParserServerWebExchangeMatcher("/api/document");

    private final ServerWebExchangeMatcher defaultMatcher = new AndServerWebExchangeMatcher(
        new PathPatternParserServerWebExchangeMatcher("/api/**"),
        new NegatedServerWebExchangeMatcher(new PathPatternParserServerWebExchangeMatcher("/api/hello"))
    );

    @Bean
    SecurityWebFilterChain documentWebFilterChain(ServerHttpSecurity http) {

        CustomDocumentAuthenticationWebFilter customDocumentAuthenticationWebFilter = new CustomDocumentAuthenticationWebFilter(
                customReactiveAuthenticationManager,
                customDocumentServerAuthenticationConverter,
                customDocumentServerAuthenticationSuccessHandler
        );

        return http
                .securityMatcher(ServerWebExchangeMatchers.matchers(documentMatcher))
                .addFilterAt(customDocumentAuthenticationWebFilter, SecurityWebFiltersOrder.FIRST)
                .authorizeExchange().anyExchange().authenticated()
                .and().headers()
                .contentSecurityPolicy(String.join("; ", "frame-ancestors 'self'"))
                .and().hsts().disable()
                .and().csrf().disable()
                .build();
    }

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .securityMatcher(ServerWebExchangeMatchers.matchers(defaultMatcher))
                .authorizeExchange().anyExchange().permitAll().and()
                .authenticationManager(customReactiveAuthenticationManager)
                .securityContextRepository(customSecurityContextRepository)
                .headers()
                .contentSecurityPolicy(String.join("; ", "frame-ancestors 'self'"))
                .and().hsts().disable()
                .and().csrf().disable()
                .build();
    }

//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    CorsWebFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.setAllowedOrigins(Arrays.asList(
//                "http://localhost:4200"
//        ));
//        config.setAllowedHeaders(Arrays.asList("*"));
//        config.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return new CorsWebFilter(source);
//    }
}
