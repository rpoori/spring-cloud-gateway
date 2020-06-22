package com.my.poc.gateway.security;

import com.my.poc.gateway.document.CustomDocumentServerAuthenticationConverter;
import com.my.poc.gateway.document.CustomDocumentServerAuthenticationSuccessHandler;
import com.my.poc.gateway.filters.JwtTokenGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class BeanConfig {

    @Bean
    @Profile("!test")
    CustomReactiveAuthenticationManager customReactiveAuthenticationManager() {
        return new CustomReactiveAuthenticationManager();
    }

    @Bean
    @Profile("!test")
    CustomSecurityContextRepository customSecurityContextRepository(
                    CustomReactiveAuthenticationManager customReactiveAuthenticationManager
            ) {
        return new CustomSecurityContextRepository(customReactiveAuthenticationManager);
    }

    @Bean
    CustomDocumentServerAuthenticationConverter customDocumentServerAuthenticationConverter() {
        return new CustomDocumentServerAuthenticationConverter();
    }

    @Bean
    CustomDocumentServerAuthenticationSuccessHandler customDocumentServerAuthenticationSuccessHandler() {
        return new CustomDocumentServerAuthenticationSuccessHandler();
    }

    @Bean
    JwtTokenGenerator jwtTokenGenerator() {
        return new JwtTokenGenerator();
    }
}
