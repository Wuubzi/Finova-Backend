package com.wuubzi.account.infrastructure.Exceptions

import com.wuubzi.account.application.DTOS.Response.ErrorResponse
import com.wuubzi.account.application.Exceptions.AccountNotFoundException
import com.wuubzi.account.utils.DateFormatter
import jakarta.validation.ValidationException
import org.apache.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException


@RestControllerAdvice
class GlobalExceptionHandler(
    private val dateFormatter: DateFormatter
) {

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            message = "Invalid request body",
            code = 400,
            exception = ex.javaClass.simpleName,
            path = dateFormatter.getDate()
        )
        return ResponseEntity.status(400).body(error)
    }


    @ExceptionHandler(AccountNotFoundException::class)
    fun handleAccountNotFoundException(ex: AccountNotFoundException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            message = ex.message,
            code = HttpStatus.SC_NOT_FOUND,
            exception = ex.javaClass.simpleName,
            path = dateFormatter.getDate()
        )
        return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(error)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            message = ex.message,
            code = HttpStatus.SC_BAD_REQUEST,
            exception = ex.javaClass.simpleName,
            path = dateFormatter.getDate()
        )
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(error)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            message = ex.message,
            code = HttpStatus.SC_BAD_REQUEST,
            exception = ex.javaClass.simpleName,
            path = dateFormatter.getDate()
        )
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(error)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(ex: NoResourceFoundException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            message = ex.message,
            code = HttpStatus.SC_NOT_FOUND,
            exception = ex.javaClass.simpleName,
            path = dateFormatter.getDate()
        )
        return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(error)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        val error = ErrorResponse(
            message = errors,
            code = HttpStatus.SC_BAD_REQUEST,
            exception = ex.javaClass.simpleName,
            path = dateFormatter.getDate()
        )
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(error)
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(ex: ValidationException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            message = ex.message ?: "Validation failed",
            code = HttpStatus.SC_BAD_REQUEST,
            exception = ex.javaClass.simpleName,
            path = dateFormatter.getDate()
        )
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(error)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            message = "An unexpected error occurred",
            code = HttpStatus.SC_INTERNAL_SERVER_ERROR,
            exception = ex.javaClass.simpleName,
            path = dateFormatter.getDate()
        )
        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(error)
    }
}