package com.my.poc.gateway.filters;

import com.sun.security.auth.UserPrincipal;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static java.util.Arrays.asList;

@Component
public class CustomJwtPermissionsGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomJwtPermissionsGatewayFilterFactory.Config> {

    @Override
    public GatewayFilter apply(CustomJwtPermissionsGatewayFilterFactory.Config config) {
        Principal dummyPrincipal = new UserPrincipal("dummy");
        return (exchange, chain) -> {
            Mono<Principal> principalMono = exchange.getPrincipal().defaultIfEmpty(dummyPrincipal);
            Mono<PermissionResponse> permissionResponseMono
                    = config.getRolesPermissionsService().fetchPermissions(principalMono);
            return permissionResponseMono.flatMap(permissionResponse -> {
                return principalMono.map(principal ->
                        config.getJwtTokenGenerator().generate(principal, permissionResponse.getPermissionsList()))
                        .map(jwtToken -> exchange.getRequest().mutate()
                        .headers(httpHeaders -> {
                                if(httpHeaders.containsKey(HttpHeaders.AUTHORIZATION)) {
                                    httpHeaders.replace(HttpHeaders.AUTHORIZATION, asList("Bearer " + jwtToken));
                                } else {
                                    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
                                }
                        })
                        .build());
            }).then(chain.filter(exchange));
        };
    }

    @Data
    public static class Config {
        private JwtTokenGenerator jwtTokenGenerator;
        private RolesPermissionsService rolesPermissionsService;
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    @Override
    public Class<Config> getConfigClass() {
        return CustomJwtPermissionsGatewayFilterFactory.Config.class;
    }
}
