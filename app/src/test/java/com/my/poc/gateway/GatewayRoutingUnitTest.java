package com.my.poc.gateway;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.my.poc.gateway.GatewayUnitTestConfig.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
public class GatewayRoutingUnitTest extends BaseGatewayTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(7000);

    @Autowired
    private RouteLocator routeLocator;

    @Value("${microservice.one.url}")
    private String microserviceOneUrl;

    @Value("${microservice.default.url}")
    private String microserviceDefaultUrl;

    @Test
    public void testRoutes() {
        StepVerifier.create(routeLocator.getRoutes())
                .expectNextMatches(r -> r.getId().equals("api-microservice-one")
                                && r.getUri().equals(getURI(microserviceOneUrl)))
                .expectNextMatches(r -> r.getUri().equals(getURI(microserviceDefaultUrl)))
                .expectComplete().verify();
    }

    @Test
    public void defaultPathRouteWorks() {
        stubFor(get(urlEqualTo("/some-unspecified-path-in-application-yml"))
                .willReturn(aResponse().withStatus(200)));

        testClient.get().uri("/some-unspecified-path-in-application-yml").exchange()
                .expectStatus()
                .isOk()
                .expectHeader().valueMatches(ROUTE_ID_HEADER, "default")
                .expectHeader().valueMatches(ROUTE_URI_HEADER, getURI(microserviceDefaultUrl).toString())
                .expectBody();
    }

    @Test
    public void apiMicroserviceOneRouteUnauthorized401() {
        testClient.get().uri("/api/microservice-one").exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody();
    }
}
