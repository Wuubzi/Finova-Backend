package com.wuubzi.gateway.IntegrationTests

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.gateway.route.RouteLocator
import reactor.test.StepVerifier

@SpringBootTest
class RoutesGatewayTests {
    @Autowired
    lateinit var routeLocator: RouteLocator;

    @Test
    fun shouldLoadRoutes() {
        StepVerifier.create(routeLocator.routes)
            .expectNextCount(4 )
        .verifyComplete()
    }
}