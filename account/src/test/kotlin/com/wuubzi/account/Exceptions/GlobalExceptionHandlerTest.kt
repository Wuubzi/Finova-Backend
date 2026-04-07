package com.wuubzi.account.Exceptions

import com.wuubzi.account.application.Exceptions.AccountNotFoundException
import com.wuubzi.account.infrastructure.Exceptions.GlobalExceptionHandler
import com.wuubzi.account.utils.DateFormatter
import jakarta.validation.ValidationException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.servlet.resource.NoResourceFoundException
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class GlobalExceptionHandlerTest {

    @Mock
    lateinit var dateFormatter: DateFormatter

    @Mock
    lateinit var httpMessageNotReadableException: HttpMessageNotReadableException

    @Mock
    lateinit var methodArgumentNotValidException: MethodArgumentNotValidException

    @Mock
    lateinit var bindingResult: BindingResult

    @Mock
    lateinit var noResourceFoundException: NoResourceFoundException

    private lateinit var handler: GlobalExceptionHandler

    private val mockDate = "2026-04-06"

    @BeforeEach
    fun setup() {
        whenever(dateFormatter.getDate()).thenReturn(mockDate)
        handler = GlobalExceptionHandler(dateFormatter)
    }

    @Test
    fun shouldHandleHttpMessageNotReadableException() {
        // WHEN
        val response = handler.handleHttpMessageNotReadableException(httpMessageNotReadableException)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid request body", response.body?.message)
        assertEquals(400, response.body?.code)
    }

    @Test
    fun shouldHandleAccountNotFoundException() {
        // GIVEN
        val ex = AccountNotFoundException("Account not found")

        // WHEN
        val response = handler.handleAccountNotFoundException(ex)

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Account not found", response.body?.message)
        assertEquals(404, response.body?.code)
    }

    @Test
    fun shouldHandleIllegalArgumentException() {
        // GIVEN
        val ex = IllegalArgumentException("Invalid argument")

        // WHEN
        val response = handler.handleIllegalArgumentException(ex)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid argument", response.body?.message)
        assertEquals(400, response.body?.code)
    }

    @Test
    fun shouldHandleIllegalStateException() {
        // GIVEN
        val ex = IllegalStateException("Invalid state")

        // WHEN
        val response = handler.handleIllegalStateException(ex)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid state", response.body?.message)
        assertEquals(400, response.body?.code)
    }

    @Test
    fun shouldHandleNoResourceFoundException() {
        // GIVEN
        whenever(noResourceFoundException.message).thenReturn("No static resource test-resource.")

        // WHEN
        val response = handler.handleNoResourceFoundException(noResourceFoundException)

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(404, response.body?.code)
    }

    @Test
    fun shouldHandleMethodArgumentNotValidException() {
        // GIVEN
        val fieldErrors = listOf(
            FieldError("object", "accountType", "Account type is required"),
            FieldError("object", "currency", "Currency is required")
        )
        whenever(methodArgumentNotValidException.bindingResult).thenReturn(bindingResult)
        whenever(bindingResult.fieldErrors).thenReturn(fieldErrors)

        // WHEN
        val response = handler.handleValidationException(methodArgumentNotValidException)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(400, response.body?.code)
        assertEquals("accountType: Account type is required, currency: Currency is required", response.body?.message)
    }

    @Test
    fun shouldHandleValidationException() {
        // GIVEN
        val ex = ValidationException("Validation failed")

        // WHEN
        val response = handler.handleValidationException(ex)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Validation failed", response.body?.message)
        assertEquals(400, response.body?.code)
    }

    @Test
    fun shouldHandleValidationExceptionWithNullMessage() {
        // GIVEN
        val ex = ValidationException(null as String?)

        // WHEN
        val response = handler.handleValidationException(ex)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Validation failed", response.body?.message)
    }

    @Test
    fun shouldHandleGenericException() {
        // GIVEN
        val ex = RuntimeException("Something went wrong")

        // WHEN
        val response = handler.handleGenericException(ex)

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("An unexpected error occurred", response.body?.message)
        assertEquals(500, response.body?.code)
    }
}

