package com.wuubzi.gateway.IntegrationTests

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
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
import java.nio.charset.StandardCharsets
import java.util.Date

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = [
    "eureka.client.enabled=false",
    "spring.cloud.config.enabled=false",
    "spring.main.allow-bean-definition-overriding=true"
])
class AuthFilterTests(
    @Autowired private val webTestClient: WebTestClient
) {

    companion object {
        const val URL_USER = "/api/v1/user"
        val wireMockServer = WireMockServer(7001)

        @JvmStatic
        @BeforeAll
        fun setup() {
            wireMockServer.start()

            // Stub para la ruta de auth (pública)
            wireMockServer.stubFor(
                get(urlPathMatching("/api/v1/auth/.*"))
                    .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"status":"OK"}"""))
            )

            // Stub para la ruta de users (protegida — solo llega si el token es válido)
            wireMockServer.stubFor(
                get(urlPathMatching("/api/v1/user/.*"))
                    .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""[]"""))
            )
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            wireMockServer.stop()
        }
    }



    @TestConfiguration
    class TestRoutesConfig {
        @Bean("routes")
        fun testRoutes(builder: RouteLocatorBuilder): RouteLocator {
            return builder.routes()
                // Ruta pública — sin AuthFilter
                .route("auth") { r ->
                    r.path("/api/v1/auth/**").uri("http://localhost:7001")
                }
                // Ruta protegida — con AuthFilter
                .route("users") { r ->
                    r.path("/api/v1/user/**")
                        .uri("http://localhost:7001")
                }
                .build()
        }
    }

    private val secret = "my-super-secret-key-my-super-secret-key"
    private val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    private fun generateValidToken(): String {
        return Jwts.builder()
            .subject("carlos")
            .expiration(Date(System.currentTimeMillis() + 60000))
            .signWith(key)
            .compact()
    }


    @Test
    fun loginEndpointShouldNotRequireAuthentication() {
        webTestClient.get()
            .uri("/api/v1/auth/login")
            .exchange()
            .expectStatus().isOk()
    }

    @Test
    fun shouldReturn401ForUnauthorized() {
        webTestClient.get()
            .uri(URL_USER)
            .exchange()
            .expectStatus().isUnauthorized()
            .expectBody()
            .jsonPath("$.error").isEqualTo("Unauthorized")
    }

    @Test
    fun shouldReturn401WhenTokenIsInvalid() {
        webTestClient.get()
            .uri(URL_USER)
            .header("Authorization", "Bearer invalid-token")
            .exchange()
            .expectStatus().isUnauthorized()
    }

    @Test
    fun shouldReturn200WhenTokenIsValid() {
        val token = generateValidToken()
        webTestClient.get()
            .uri(URL_USER)
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isOk()
    }
}