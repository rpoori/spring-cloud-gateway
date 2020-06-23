package com.my.poc.gateway.integrationcontracts;

import com.google.gson.Gson;
import com.my.poc.gateway.GatewayApplication;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.time.Duration;

import static com.my.poc.gateway.integrationcontracts.GatewayIntegrationTestConfig.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GatewayApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Slf4j
public class GatewayRoutingIntegrationTest {

    @LocalServerPort
    protected int port = 0;

    protected String baseUri;
    protected WebClient webClient;
    protected WebTestClient testClient;

    @Autowired
    private RouteLocator routeLocator;

    @Value("${microservice.one.url}")
    private String microserviceOneUrl;

    @Value("${microservice.two.url}")
    private String microserviceTwoUrl;

    @Value("${microservice.default.url}")
    private String microserviceDefaultUrl;

    @Before
    public void setup() {
        ClientHttpConnector httpConnector = new ReactorClientHttpConnector();

        baseUri = "http://localhost:" + port;

        this.webClient = WebClient.builder()
                .clientConnector(httpConnector)
                .baseUrl(baseUri)
                .build();

        this.testClient = WebTestClient.bindToServer()
                .baseUrl(baseUri)
                .responseTimeout(Duration.ofSeconds(30L))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(500000))
                        .build())
                .build();
    }

    @Test
    public void testRoutes() {
        StepVerifier.create(routeLocator.getRoutes())
                .expectNextMatches(r -> r.getId().equals("api-microservice-one")
                                && r.getUri().equals(getURI(microserviceOneUrl)))
                .expectNextMatches(r -> r.getId().equals("api-microservice-two")
                        && r.getUri().equals(getURI(microserviceTwoUrl)))
                .expectNextMatches(r -> r.getUri().equals(getURI(microserviceDefaultUrl)))
                .expectComplete().verify();
    }

    @Test
    public void defaultPathRouteWorks() {
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

    @Test
    public void apiMicroserviceOneRouteAuthorized200() {

        SampleRequest sampleRequest = SampleRequest.builder().id("id").name("name").build();

        testClient.post().uri("/api/microservice-one")
                .header(HttpHeaders.AUTHORIZATION, "Bearer 987654")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new Gson().toJson(sampleRequest)))
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().valueMatches(ROUTE_ID_HEADER, "api-microservice-one")
                .expectHeader().valueMatches(ROUTE_URI_HEADER, getURI(microserviceOneUrl).toString())
                .expectBody();
    }
}

@Data
@Builder
class SampleRequest {
    String id;
    String name;
}
