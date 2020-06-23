package com.my.poc.gateway.filters;

import com.sun.security.auth.UserPrincipal;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Arrays;

@Component
public class CustomJwtGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomJwtGatewayFilterFactory.Config> {

    @Override
    public GatewayFilter apply(CustomJwtGatewayFilterFactory.Config config) {
        Principal dummyPrincipal = new UserPrincipal("dummy");
        return (exchange, chain) ->
                exchange.getPrincipal()
                .defaultIfEmpty(dummyPrincipal)
                .flatMap(p -> {
                    String jwtToken = config.getJwtTokenGenerator().generate(p);
                    if(exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                        exchange.getRequest().mutate()
                                .headers(httpHeaders -> httpHeaders.replace(HttpHeaders.AUTHORIZATION, Arrays.asList(("Bearer " + jwtToken))))
                                .build();
                    } else {
                        exchange.getRequest().mutate()
                                .headers(httpHeaders -> httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                                .build();
                    }
                    return chain.filter(exchange);
                });
    }

    @Data
    public static class Config {
        private JwtTokenGenerator jwtTokenGenerator;
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    @Override
    public Class<Config> getConfigClass() {
        return CustomJwtGatewayFilterFactory.Config.class;
    }
}
