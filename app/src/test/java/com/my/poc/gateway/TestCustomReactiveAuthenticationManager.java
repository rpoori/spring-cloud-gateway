package com.my.poc.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Slf4j
public class TestCustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        // Authentication logic
        boolean isAuthorized = authToken != null && (TestSecurityConfig.TEST_BEARER_TOKEN).equals(authToken);
        if(authToken.isEmpty() || !isAuthorized) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials");
        }

        Object principal = authentication.getPrincipal() != null ? authentication.getPrincipal() : "testuserid";
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, authToken, new ArrayList<>());
        SecurityContextHolder.setContext(new SecurityContextImpl(authenticationToken));
        log.info("Test authentication successful");
        return Mono.just(authenticationToken);
    }
}
