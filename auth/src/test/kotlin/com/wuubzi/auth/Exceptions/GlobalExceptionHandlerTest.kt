package com.wuubzi.auth.Exceptions

import com.wuubzi.auth.Utils.DateFormatter
import com.wuubzi.auth.application.Exceptions.EmailAlreadyExist
import com.wuubzi.auth.application.Exceptions.InvalidOtpException
import com.wuubzi.auth.application.Exceptions.TokenExpiredException
import com.wuubzi.auth.application.Exceptions.TokenNotFoundException
import com.wuubzi.auth.infrastructure.Exceptions.GlobalExceptionHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.http.HttpMethod
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.servlet.resource.NoResourceFoundException
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class GlobalExceptionHandlerTest {

    @Mock
    lateinit var dateFormatter: DateFormatter

    private lateinit var exceptionHandler: GlobalExceptionHandler
    private val mockDate = "2026-03-12"

    @BeforeEach
    fun setup() {
        exceptionHandler = GlobalExceptionHandler(dateFormatter)
        whenever(dateFormatter.getDate()).thenReturn(mockDate)
    }

    @Test
    fun shouldHandleEmailAlreadyExist() {
        val ex = EmailAlreadyExist("Email exists")
        val response = exceptionHandler.handleEmailAlreadyExistException(ex)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("Email exists", response.body?.message)
        assertEquals(mockDate, response.body?.path)
    }

    @Test
    fun shouldHandleIllegalArgumentException() {
        val ex = IllegalArgumentException("Invalid argument")
        val response = exceptionHandler.handleIllegalArgumentException(ex)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("IllegalArgumentException", response.body?.exception)
    }

    @Test
    fun shouldHandleTokenNotFoundException() {
        val ex = TokenNotFoundException("Token not found")
        val response = exceptionHandler.handleTokenNotFoundException(ex)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals(401, response.body?.code)
    }

    @Test
    fun shouldHandleTokenExpiredException() {
        val ex = TokenExpiredException("Expired")
        val response = exceptionHandler.handleTokenExpiredException(ex)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals("TokenExpiredException", response.body?.exception)
    }

    @Test
    fun shouldHandleInvalidOtpException() {
        val ex = InvalidOtpException("Invalid OTP")
        val response = exceptionHandler.handleInvalidOtpException(ex)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid OTP", response.body?.message)
    }

    @Test
    fun shouldHandleHttpMessageNotReadableException() {
        val ex = mock(HttpMessageNotReadableException::class.java)
        val response = exceptionHandler.handleHttpMessageNotReadableException(ex)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid request body", response.body?.message)
    }

    @Test
    fun shouldHandleNoResourceFoundException() {
        // NoResourceFoundException requiere parámetros en el constructor
        val ex = NoResourceFoundException(HttpMethod.POST, "/path", "/path")
        val response = exceptionHandler.handleNoResourceFoundException(ex)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun shouldHandleGenericException() {
        val ex = Exception("Generic error")
        val response = exceptionHandler.handleGenericException(ex)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("An unexpected error occurred", response.body?.message)
    }
}