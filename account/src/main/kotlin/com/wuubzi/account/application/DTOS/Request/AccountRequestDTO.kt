package com.wuubzi.account.application.DTOS.Request

data class AccountRequestDTO (
    val accountType: String,
    val currency: String,
    val balance: Double,
    val availableBalance: Double,
    val overdraftLimit: Double,
    val status: String,
    val alias: String,
)