package com.wuubzi.gateway.Config

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayConfig {

    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("auth") { r -> r.path("api/v1/auth/**").uri("http://localhost:8010") }
            .route("user") { r -> r.path("api/v1/user/**").uri("http://localhost:8020") }
            .route("transaction") { r -> r.path("api/v1/transaction/**").uri("http://localhost:8030") }
            .route("account") { r -> r.path("api/v1/account/**").uri("http://localhost:8040")  }
            .build()
    }

}