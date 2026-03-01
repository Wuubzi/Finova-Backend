package com.wuubzi.gateway.IntegrationTests

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.test.context.TestPropertySource

import org.springframework.test.web.reactive.server.WebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = [
    "eureka.client.enabled=false",
    "spring.cloud.config.enabled=false",
    "spring.main.allow-bean-definition-overriding=true"
])
class RoutesValidTests(
    @Autowired private val webTestClient: WebTestClient
) {

    @TestConfiguration
    class TestRoutesConfig {
        @Bean("routes")  // mismo nombre para sobreescribir
        fun testRoutes(builder: RouteLocatorBuilder): RouteLocator {
            return builder.routes()
                .route("auth") { r -> r.path("/api/v1/auth/**").uri("http://localhost:7000") }
                .build()
        }
    }

    companion object {
        val wireMockServer = WireMockServer(7000)

        @JvmStatic
        @BeforeAll
        fun setup() {
            wireMockServer.start()
            wireMockServer.stubFor(
                get(urlEqualTo("/api/v1/auth/actuator/health"))
                    .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"status":"UP"}"""))
            )
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            wireMockServer.stop()
        }
    }

    @Test
    fun shouldReturn404ForUnknownRoute() {
        webTestClient.get()
            .uri("/unknown-route")
            .exchange()
            .expectStatus().isNotFound()
    }

    @Test
    fun shouldResolveRoutes() {
        webTestClient.get()
            .uri("/api/v1/auth/actuator/health")
            .exchange()
            .expectStatus().isOk()
    }
}