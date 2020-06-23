package com.my.poc.gateway.security;

import com.my.poc.gateway.document.CustomDocumentServerAuthenticationConverter;
import com.my.poc.gateway.document.CustomDocumentServerAuthenticationSuccessHandler;
import com.my.poc.gateway.filters.JwtTokenGenerator;
import com.my.poc.gateway.filters.RolesPermissionsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

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

    @Bean
    RolesPermissionsService rolesPermissionsService() {
        return new RolesPermissionsService();
    }

    @Bean
    WebClient webClient() {
        return WebClient.builder().build();
    }
}
