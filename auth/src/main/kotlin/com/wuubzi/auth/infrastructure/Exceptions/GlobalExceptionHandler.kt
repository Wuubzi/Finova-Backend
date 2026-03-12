package com.wuubzi.auth.infrastructure.Exceptions

import com.wuubzi.auth.Utils.DateFormatter
import com.wuubzi.auth.application.Exceptions.EmailAlreadyExist
import com.wuubzi.auth.application.DTOS.Response.ErrorResponse
import com.wuubzi.auth.application.Exceptions.InvalidOtpException
import com.wuubzi.auth.application.Exceptions.TokenExpiredException
import com.wuubzi.auth.application.Exceptions.TokenNotFoundException
import com.wuubzi.auth.application.Exceptions.TokenRevokedException
import org.apache.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
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

    @ExceptionHandler(EmailAlreadyExist::class)
    fun handleEmailAlreadyExistException(ex: EmailAlreadyExist): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            message = ex.message,
            code = HttpStatus.SC_CONFLICT,
            exception = ex.javaClass.simpleName,
            path = dateFormatter.getDate()
        )
        return ResponseEntity.status(HttpStatus.SC_CONFLICT).body(error)
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

    @ExceptionHandler(InvalidOtpException::class)
    fun handleInvalidOtpException(ex: InvalidOtpException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            message = ex.message,
            code = HttpStatus.SC_BAD_REQUEST,
            exception = ex.javaClass.simpleName,
            path = dateFormatter.getDate()
        )
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(error)
    }

    @ExceptionHandler(TokenNotFoundException::class)
    fun handleTokenNotFoundException(ex: TokenNotFoundException): ResponseEntity<ErrorResponse> = ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body(
        ErrorResponse(
            message = ex.message,
            code = HttpStatus.SC_UNAUTHORIZED,
            exception = ex.javaClass.simpleName,
            path = dateFormatter.getDate()
        )

    )

    @ExceptionHandler(TokenRevokedException::class)
    fun handleTokenRevokedException(ex: TokenRevokedException): ResponseEntity<ErrorResponse> = ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body(
        ErrorResponse(
            message = ex.message,
            code = HttpStatus.SC_UNAUTHORIZED,
            exception = ex.javaClass.simpleName,
            path = dateFormatter.getDate()
        )
    )

    @ExceptionHandler(TokenExpiredException::class)
    fun handleTokenExpiredException(ex: TokenExpiredException): ResponseEntity<ErrorResponse> = ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body(
        ErrorResponse(
            message = ex.message,
            code = HttpStatus.SC_UNAUTHORIZED,
            exception = ex.javaClass.simpleName,
            path = dateFormatter.getDate()
        )
    )

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