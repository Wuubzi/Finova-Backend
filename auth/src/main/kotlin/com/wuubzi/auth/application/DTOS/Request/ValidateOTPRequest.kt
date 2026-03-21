package com.wuubzi.auth.application.DTOS.Request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ValidateOTPRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank(message = "OTP is required")
    @field:Size(min = 6, max = 6, message = "OTP must be exactly 6 digits")
    @field:Pattern(
        regexp = "^[0-9]{6}$",
        message = "OTP must contain only digits"
    )
    val otp: String
)