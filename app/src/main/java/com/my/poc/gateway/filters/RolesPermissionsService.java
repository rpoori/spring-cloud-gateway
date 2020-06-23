package com.my.poc.gateway.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static java.util.Arrays.asList;

public class RolesPermissionsService {

    @Autowired
    private WebClient webClient;

    @Value("{$roles-permissions-service-url}")
    private String rolesPermissionsServiceUrl;

    public Mono<PermissionResponse> fetchPermissions(Mono<Principal> principalMono) {
        return Mono.just(PermissionResponse.builder().permissionsList(asList("READ","WRITE")).build());
//        return principalMono.map(Principal::getName)
//                .map(principalName -> webClient.get()
//                .uri(rolesPermissionsServiceUrl + "/" + principalName + "/permissions")
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .retrieve().bodyToMono(PermissionResponse.class))
//                .flatMap(permissionResponseMono -> permissionResponseMono.flatMap(Mono::just));
    }
}

