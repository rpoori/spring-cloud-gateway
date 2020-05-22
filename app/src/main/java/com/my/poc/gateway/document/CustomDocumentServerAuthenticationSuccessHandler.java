package com.my.poc.gateway.document;

import com.my.poc.gateway.filters.JwtTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class CustomDocumentServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        DocumentRequestDto documentRequestDto = (DocumentRequestDto) authentication.getPrincipal();
        Map<String, Object> credentials = new LinkedHashMap<>();
        credentials.put("credentials", authentication.getCredentials());

        String jwtToken = jwtTokenGenerator.generate(credentials);

        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());

        ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {

            @Override
            public HttpMethod getMethod() { return HttpMethod.POST; }

            @Override
            public String getMethodValue() { return "POST"; }

            @Override
            public URI getURI() {
                URI uri = getDelegate().getURI();
                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.put("documentId", singletonList(documentRequestDto.getDocumentId()));
                return UriComponentsBuilder.fromUri(uri).queryParams(map).build().toUri();
            }

            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                httpHeaders.replace(HttpHeaders.AUTHORIZATION, asList("Bearer " + jwtToken));
                httpHeaders.replace(HttpHeaders.CONTENT_TYPE, singletonList(MediaType.APPLICATION_JSON_VALUE));

                if(contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return Flux.empty();
            }
        };

        return webFilterExchange.getChain().filter(exchange.mutate().request(decorator).build());
    }
}
