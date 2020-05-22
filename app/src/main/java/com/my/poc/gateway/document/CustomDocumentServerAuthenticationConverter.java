package com.my.poc.gateway.document;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CustomDocumentServerAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange.getFormData().map(this::createAuthentication);
    }

    private UsernamePasswordAuthenticationToken createAuthentication(MultiValueMap<String, String> multiValueMap) {
        DocumentRequestDto documentRequestDto = DocumentRequestDto.builder()
                .documentId(multiValueMap.getFirst("documentId"))
                .bearerToken(multiValueMap.getFirst("bearerToken"))
                .build();

        return new UsernamePasswordAuthenticationToken(documentRequestDto, documentRequestDto.getBearerToken());
    }
}
