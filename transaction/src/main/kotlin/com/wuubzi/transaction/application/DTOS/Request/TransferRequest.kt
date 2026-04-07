package com.wuubzi.transaction.application.DTOS.Request

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class TransferRequest(
    @field:NotBlank(message = "Source account number is required")
    @field:Size(min = 10, max = 20, message = "Source account number must be between 10 and 20 characters")
    val fromAccountNumber: String,

    @field:NotBlank(message = "Destination account number is required")
    @field:Size(min = 10, max = 20, message = "Destination account number must be between 10 and 20 characters")
    val toAccountNumber: String,

    @field:Positive(message = "Amount must be greater than zero")
    @field:DecimalMin(value = "0.01", message = "Minimum transfer amount is 0.01")
    val amount: Double
)

