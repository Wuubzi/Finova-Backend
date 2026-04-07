package com.wuubzi.transaction.Security

import com.wuubzi.transaction.infrastructure.Security.CustomAuthenticationEntryPoint
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.core.AuthenticationException
import tools.jackson.databind.ObjectMapper
import java.io.PrintWriter

@ExtendWith(MockitoExtension::class)
class CustomAuthenticationEntryPointTest {

    @Mock
    lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var request: HttpServletRequest

    @Mock
    lateinit var response: HttpServletResponse

    @Mock
    lateinit var authException: AuthenticationException

    @Mock
    lateinit var writer: PrintWriter

    @Test
    fun shouldReturnUnauthorizedResponse() {
        // GIVEN
        whenever(request.requestURI).thenReturn("/api/v1/transaction")
        whenever(response.writer).thenReturn(writer)
        whenever(objectMapper.writeValueAsString(any<Map<String, Any>>())).thenReturn("{\"message\":\"Unauthorized\"}")

        val entryPoint = CustomAuthenticationEntryPoint(objectMapper)

        // WHEN
        entryPoint.commence(request, response, authException)

        // THEN
        verify(response).status = HttpServletResponse.SC_UNAUTHORIZED
        verify(response).contentType = "application/json"
        verify(writer).write(any<String>())
    }
}

