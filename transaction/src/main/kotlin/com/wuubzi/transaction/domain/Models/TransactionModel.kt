package com.wuubzi.transaction.domain.Models

import java.sql.Timestamp
import java.util.UUID

data class TransactionModel (
    val transactionId: UUID,
    val fromAccountId: UUID?,
    val toAccountId: UUID?,
    val amount: Double,
    val currency : String,
    val status: String,
    val type: String,
    val description: String,
    val createdAt: Timestamp
    )