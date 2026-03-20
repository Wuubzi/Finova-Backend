package com.wuubzi.auth.application.DTOS.Request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RecoverPasswordRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String
)