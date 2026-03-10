package com.wuubzi.gateway.IntegrationTests

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier

@ActiveProfiles("test")
@SpringBootTest
class RoutesGatewayTests {

    @Autowired
    lateinit var routeLocator: RouteLocator

    @TestConfiguration
    class TestRoutesConfig {
        @Bean
        fun testRoutes(builder: RouteLocatorBuilder): RouteLocator {
            return builder.routes()
                .route("auth") { it.path("/api/v1/auth/**").uri("http://localhost:7001") }
                .route("user") { it.path("/api/v1/user/**").uri("http://localhost:7001") }
                .route("transaction") { it.path("/api/v1/transaction/**").uri("http://localhost:7001") }
                .route("account") { it.path("/api/v1/account/**").uri("http://localhost:7001") }
                .build()
        }
    }

    @Test
    fun shouldLoadRoutes() {
        StepVerifier.create(routeLocator.routes)
            .expectNextCount(4)
            .verifyComplete()
    }
}