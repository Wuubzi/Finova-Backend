package com.wuubzi.auth.infrastructure.Exceptions

import com.wuubzi.auth.infrastructure.Persistence.DTOS.Response.ErrorResponse
import org.apache.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            message = "Invalid request body",
            code = 400,
            exception = ex.javaClass.simpleName,
            path = null
        )
        return ResponseEntity.status(400).body(error)
    }
}