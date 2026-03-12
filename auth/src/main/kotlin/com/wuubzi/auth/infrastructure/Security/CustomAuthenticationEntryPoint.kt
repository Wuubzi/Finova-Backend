package com.wuubzi.auth.infrastructure.Security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class CustomAuthenticationEntryPoint (
    private val objectMapper: ObjectMapper
): AuthenticationEntryPoint{
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val body = mapOf(
            "message" to "No estas autorizado para acceder a este recurso",
            "status" to HttpServletResponse.SC_UNAUTHORIZED,
            "exception" to authException::class.java.simpleName,
            "path" to request.requestURI,
            "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        response.writer.write(objectMapper.writeValueAsString(body))
    }


}