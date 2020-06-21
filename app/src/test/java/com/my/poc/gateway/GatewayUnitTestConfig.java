package com.my.poc.gateway;

import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Configuration
@Slf4j
public class GatewayUnitTestConfig {

    public static final String ROUTE_ID_HEADER = "X-Gateway-RouteDefinition_Id";
    public static final String ROUTE_URI_HEADER = "X-Gateway-RouteDefinition_URI";

    public static URI getURI(String uriStr) {
        URI uri = URI.create(uriStr);
        String scheme = uri.getScheme();
        Assert.hasText(scheme, "The parameter [" + uri + "] format is incorrect, scheme can not be empty");
        if (uri.getPort() < 0 && scheme.startsWith("http")) {
            int port = uri.getScheme().equals("https") ? 443 : 80;
            uri = UriComponentsBuilder.fromUri(uri).port(port).build(false).toUri();
        }
        return uri;
    }

    @Bean
    @Order(500)
    public GlobalFilter routeIdResponseFilter() {
        return (exchange, chain) -> {
            log.info("routeIdResponseFilter...");
            Route route = exchange.getAttributeOrDefault(GATEWAY_ROUTE_ATTR, null);
            if (route != null) {
                exchange.getResponse().getHeaders().add(ROUTE_ID_HEADER, route.getId());
                exchange.getResponse().getHeaders().add(ROUTE_URI_HEADER, route.getUri().toString());
            }
            return chain.filter(exchange);
        };
    }
}
