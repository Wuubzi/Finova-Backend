package com.wuubzi.user.Security

import com.wuubzi.user.infrastructure.Security.CustomAuthenticationEntryPoint
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
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
    lateinit var printWriter: PrintWriter // Ahora es un Mock

    @InjectMocks
    lateinit var entryPoint: CustomAuthenticationEntryPoint

    @Test
    fun shouldWriteUnauthorizedErrorResponse() {
        // GIVEN
        val jsonResponse = "{\"json\":\"mocked\"}"
        whenever(request.requestURI).thenReturn("/api/test")
        whenever(response.writer).thenReturn(printWriter)
        whenever(objectMapper.writeValueAsString(any<Map<String, Any>>())).thenReturn(jsonResponse)

        // WHEN
        entryPoint.commence(request, response, authException)

        // THEN
        verify(response).status = HttpServletResponse.SC_UNAUTHORIZED
        verify(response).contentType = "application/json"

        // Ahora sí podemos verificar el writer porque es un mock
        verify(printWriter).write(jsonResponse)

        // Verificamos el contenido del mapa para SonarQube
        val captor = argumentCaptor<Map<String, Any>>()
        verify(objectMapper).writeValueAsString(captor.capture())

        val body = captor.firstValue
        assertEquals("You are not authorized to access this resource.", body["message"])
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, body["status"])
        assertEquals("/api/test", body["path"])
    }
}