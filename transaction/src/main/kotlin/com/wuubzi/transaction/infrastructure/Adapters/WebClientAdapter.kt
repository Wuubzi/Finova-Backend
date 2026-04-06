package com.wuubzi.transaction.infrastructure.Adapters

import com.wuubzi.transaction.application.DTOS.Response.AccountResponse
import com.wuubzi.transaction.application.Ports.Out.WebClientPort
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class WebClientAdapter: WebClientPort {
    override fun getAccountId(accountId: UUID): AccountResponse? {
        val token = getCurrentToken()
        val webClient = WebClient.create("http://localhost:8000/api/v1/account")
        return webClient.get()
            .uri("", accountId)
            .apply {
                if (token != null) {
                    headers { it.setBearerAuth(token) }
                }
            }
            .retrieve()
            .bodyToMono<AccountResponse>()
            .onErrorResume(WebClientResponseException::class.java) { Mono.empty() }
            .block()
    }

    override fun getAccountByAccountNumber(accountNumber: String): AccountResponse? {
        val token = getCurrentToken()
        val webClient = WebClient.create("http://localhost:8000/api/v1/account")
        return webClient.get()
            .uri("/{accountNumber}", accountNumber)
            .apply {
                if (token != null) {
                    headers { it.setBearerAuth(token) }
                }
            }
            .retrieve()
            .bodyToMono<AccountResponse>()
            .onErrorResume(WebClientResponseException::class.java) { Mono.empty() }
            .block()
    }

    private fun getCurrentToken(): String? {
        val requestAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        val request: HttpServletRequest? = requestAttributes?.request
        val authHeader = request?.getHeader("Authorization")
        return if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader.substring(7)
        } else {
            null
        }
    }
}