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

const val api_url = "http://localhost:7001"

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
                .route("auth") { it.path("/api/v1/auth/**").uri(api_url) }
                .route("user") { it.path("/api/v1/user/**").uri(api_url) }
                .route("transaction") { it.path("/api/v1/transaction/**").uri(api_url) }
                .route("account") { it.path("/api/v1/account/**").uri(api_url) }
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