package com.my.poc.gateway.document;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;

public class CustomDocumentAuthenticationWebFilter extends AuthenticationWebFilter {

    public CustomDocumentAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager,
                    ServerAuthenticationConverter serverAuthenticationConverter,
                    ServerAuthenticationSuccessHandler serverAuthenticationSuccessHandler
            ) {
        super(authenticationManager);
        setServerAuthenticationConverter(serverAuthenticationConverter);
        setAuthenticationSuccessHandler(serverAuthenticationSuccessHandler);
    }
}
