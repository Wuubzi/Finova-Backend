package com.wuubzi.account.domain.models

import java.sql.Timestamp
import java.util.UUID


data class AccountModel (
    val accountId: UUID,
    val accountNumber: String,
    val userId: UUID,
    val accountType: String,
    val currency: String,
    val balance: Double,
    val availableBalance: Double,
    val status: String,
    val alias: String,
    val overdraftLimit: Double,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
)