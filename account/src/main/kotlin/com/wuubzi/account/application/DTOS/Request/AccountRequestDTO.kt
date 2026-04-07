package com.wuubzi.account.application.DTOS.Request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

data class AccountRequestDTO(
    @field:NotBlank(message = "Account type is required")
    @field:Pattern(
        regexp = "^(SAVINGS|CHECKING)$",
        message = "Account type must be SAVINGS or CHECKING"
    )
    val accountType: String,

    @field:NotBlank(message = "Currency is required")
    @field:Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    val currency: String,

    @field:PositiveOrZero(message = "Balance must be zero or positive")
    val balance: Double,

    @field:PositiveOrZero(message = "Available balance must be zero or positive")
    val availableBalance: Double,

    @field:PositiveOrZero(message = "Overdraft limit must be zero or positive")
    val overdraftLimit: Double,

    @field:NotBlank(message = "Status is required")
    @field:Pattern(
        regexp = "^(ACTIVE|INACTIVE|BLOCKED)$",
        message = "Status must be ACTIVE, INACTIVE or BLOCKED"
    )
    val status: String,

    @field:NotBlank(message = "Alias is required")
    @field:Size(min = 1, max = 100, message = "Alias must be between 1 and 100 characters")
    val alias: String,
)