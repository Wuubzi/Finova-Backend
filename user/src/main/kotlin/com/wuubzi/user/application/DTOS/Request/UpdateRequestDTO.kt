package com.wuubzi.user.application.DTOS.Request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateRequestDTO(
    @field:NotBlank(message = "First name is required")
    @field:Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    @field:Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    val lastName: String,

    @field:NotBlank(message = "Document number is required")
    @field:Pattern(
        regexp = "^[0-9]{7,15}$",
        message = "Document number must contain only digits and be betw een 7 and 15 characters"
    )
    val documentNumber: String,

    @field:NotBlank(message = "Phone number is required")
    @field:Pattern(
        regexp = "^\\+?[0-9]{10,15}$",
        message = "Phone number must be valid and contain 10 to 15 digits"
    )
    val phone: String,

    @field:NotBlank(message = "Address is required")
    @field:Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    val address: String,
)