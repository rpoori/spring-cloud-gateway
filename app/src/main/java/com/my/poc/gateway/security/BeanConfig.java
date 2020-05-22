package com.my.poc.gateway.security;

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
}
