package com.wuubzi.account.application.DTOS.Response

data class  AccountBalanceResponse (
    val balance: Double,
    val availableBalance: Double,
    val currency: String,
    val  overdraftLimit: Double
)