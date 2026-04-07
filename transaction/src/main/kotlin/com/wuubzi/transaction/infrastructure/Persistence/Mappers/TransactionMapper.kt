package com.wuubzi.transaction.infrastructure.Persistence.Mappers

import com.wuubzi.transaction.domain.Models.TransactionModel
import com.wuubzi.transaction.infrastructure.Persistence.Entities.TransactionEntity
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

fun TransactionModel.toEntity(): TransactionEntity {
    return TransactionEntity().apply {
        transactionId = this@toEntity.transactionId
        fromAccountId = this@toEntity.fromAccountId
        toAccountId = this@toEntity.toAccountId
        amount = this@toEntity.amount
        currency = this@toEntity.currency
        status = this@toEntity.status
        type = this@toEntity.type
        description = this@toEntity.description
        createdAt = this@toEntity.createdAt
    }
}

fun TransactionEntity.toDomain(): TransactionModel {
    return TransactionModel(
        transactionId = transactionId ?: UUID.randomUUID(),
        fromAccountId = fromAccountId,
        toAccountId = toAccountId,
        amount = amount ?: 0.0,
        currency = currency ?: "USD",
        status = status ?: "PENDING",
        type = type ?: "TRANSFER",
        description = description ?: "",
        createdAt = createdAt ?: Timestamp.from(Instant.now())
    )
}