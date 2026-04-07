package com.wuubzi.account.application.DTOS.Events

import java.time.Instant
import java.util.UUID

data class TransactionEvent(
    val transactionId: UUID,
    val eventType: EventType,
    val type: TransactionType,
    val amount: Double,
    val fromAccountId: UUID? = null,
    val toAccountId: UUID? = null,
    val currency: String,
    val status: TransactionStatus,
    val description: String? = null,
    val email: String? = null,
    val timestamp: Instant = Instant.now()
)

enum class EventType {
    TRANSACTION_CREATED,
    TRANSACTION_COMPLETED,
    TRANSACTION_FAILED
}

enum class TransactionType {
    DEPOSIT,
    WITHDRAW,
    TRANSFER
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED
}

