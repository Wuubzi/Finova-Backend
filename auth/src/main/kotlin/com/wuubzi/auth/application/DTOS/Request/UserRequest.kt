package com.wuubzi.auth.application.DTOS.Request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserRequest(
    @field:NotBlank(message = "First name is required")
    @field:Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    @field:Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    val lastName: String,

    @field:NotBlank(message = "Document number is required")
    @field:Pattern(
        regexp = "^[0-9]{7,15}$",
        message = "Document number must contain only digits and be between 7 and 15 characters"
    )
    val documentNumber: String,

    @field:NotBlank(message = "Phone number is required")
    @field:Pattern(
        regexp = "^\\+?[0-9]{10,15}$",
        message = "Phone number must be valid and contain 10 to 15 digits"
    )
    val phoneNumber: String,

    @field:NotBlank(message = "Address is required")
    @field:Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    val address: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character"
    )
    val password: String,
)