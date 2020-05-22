package com.my.poc.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.*;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private CustomReactiveAuthenticationManager customReactiveAuthenticationManager;

    @Autowired
    private CustomSecurityContextRepository customSecurityContextRepository;

    private final ServerWebExchangeMatcher defaultMatcher = new AndServerWebExchangeMatcher(
        new PathPatternParserServerWebExchangeMatcher("/api/**"),
        new NegatedServerWebExchangeMatcher(new PathPatternParserServerWebExchangeMatcher("/api/hello"))
    );

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
}
