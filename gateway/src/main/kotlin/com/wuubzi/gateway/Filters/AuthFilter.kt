package com.wuubzi.gateway.Filters

import io.jsonwebtoken.JwtException
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.http.HttpStatus
import org.springframework.core.Ordered
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import com.fasterxml.jackson.databind.ObjectMapper
import com.wuubzi.gateway.Security.JwtService

@Suppress("Kotlin:S6508")
@Component
class AuthFilter(
    private val jwtService: JwtService,
    private val objectMapper: ObjectMapper
): GlobalFilter, Ordered {


    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {

        val path = exchange.request.uri.path

        if (path.startsWith("/api/v1/auth")) {
            return chain.filter(exchange)
        }

        val authHeader = exchange.request.headers.getFirst("Authorization")
            ?: return unauthorized(exchange, "Missing Authorization header")

        if (!authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "invalid Authorization header")
        }

        val token = authHeader.substring(7)

        return try {

            val userId = jwtService.validateToken(token)

            val mutatedExchange = exchange.mutate()
                .request(
                    exchange.request.mutate()
                        .header("X-User-Id", userId)
                        .build()
                )
                .build()


            chain.filter(mutatedExchange)

        } catch (ex: JwtException) {
           unauthorized(exchange, "Invalid or expired token")
        }


    }

    override fun getOrder(): Int = -1

    private fun unauthorized(
        exchange: ServerWebExchange,
        message: String
    ): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON

        val body = mapOf(
            "status" to 401,
            "error" to "Unauthorized",
            "message" to message,
            "path" to exchange.request.uri.path
        )

        val bytes = objectMapper.writeValueAsBytes(body)
        val buffe: DataBuffer = response.bufferFactory().wrap(bytes)

        return response.writeWith(Mono.just(buffe))
    }
}