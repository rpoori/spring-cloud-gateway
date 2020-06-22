package com.my.poc.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.*;

@Configuration
@EnableWebFluxSecurity
@Slf4j
@Profile("test")
public class TestSecurityConfig {

    public static final String TEST_BEARER_TOKEN = "test-bearer-token";

    @Autowired
    private TestCustomReactiveAuthenticationManager testCustomReactiveAuthenticationManager;

    @Autowired
    private TestCustomSecurityContextRepository testCustomSecurityContextRepository;

    private final ServerWebExchangeMatcher defaultMatcher = new AndServerWebExchangeMatcher(
        new PathPatternParserServerWebExchangeMatcher("/api/**"),
        new NegatedServerWebExchangeMatcher(new PathPatternParserServerWebExchangeMatcher("/api/hello"))
    );

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .securityMatcher(ServerWebExchangeMatchers.matchers(defaultMatcher))
                .authorizeExchange().anyExchange().permitAll().and()
                .authenticationManager(testCustomReactiveAuthenticationManager)
                .securityContextRepository(testCustomSecurityContextRepository)
                .headers()
                .contentSecurityPolicy(String.join("; ", "frame-ancestors 'self'"))
                .and().hsts().disable()
                .and().csrf().disable()
                .build();
    }

    @Bean
    @Profile("test")
    TestCustomReactiveAuthenticationManager testCustomReactiveAuthenticationManager() {
        return new TestCustomReactiveAuthenticationManager();
    }

    @Bean
    @Profile("test")
    TestCustomSecurityContextRepository testCustomSecurityContextRepository() {
        return new TestCustomSecurityContextRepository(testCustomReactiveAuthenticationManager);
    }
}
