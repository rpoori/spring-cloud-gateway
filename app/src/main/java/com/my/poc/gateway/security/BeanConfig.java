package com.my.poc.gateway.security;

import com.my.poc.gateway.filters.JwtTokenGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    CustomReactiveAuthenticationManager customReactiveAuthenticationManager() {
        return new CustomReactiveAuthenticationManager();
    }

    @Bean
    CustomSecurityContextRepository customSecurityContextRepository(
                    CustomReactiveAuthenticationManager customReactiveAuthenticationManager
            ) {
        return new CustomSecurityContextRepository(customReactiveAuthenticationManager);
    }

    @Bean
    JwtTokenGenerator jwtTokenGenerator() {
        return new JwtTokenGenerator();
    }
}
