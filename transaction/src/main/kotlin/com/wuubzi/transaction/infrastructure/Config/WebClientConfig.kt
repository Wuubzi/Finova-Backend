package com.wuubzi.transaction.infrastructure.Config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun accountWebClient(): WebClient {
        return WebClient.builder()
            .build()
    }
}