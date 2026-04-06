package com.wuubzi.transaction.application.DTOS.Request

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class DepositRequest(
    @field:NotBlank(message = "Account number is required")
    @field:Size(min = 10, max = 20, message = "Account number must be between 10 and 20 characters")
    val accountNumber: String,

    @field:Positive(message = "Amount must be greater than zero")
    @field:DecimalMin(value = "0.01", message = "Minimum deposit amount is 0.01")
    val amount: Double
)

