package com.wuubzi.account.infrastructure.Persistence.Mappers

import com.wuubzi.account.domain.models.AccountModel
import com.wuubzi.account.infrastructure.Persistence.Entities.AccountEntity
import java.sql.Timestamp
import java.time.Instant

fun AccountModel.toEntity(): AccountEntity {
    return AccountEntity().apply {
        idAccount = this@toEntity.accountId
        accountNumber = this@toEntity.accountNumber
        userId = this@toEntity.userId
        accountType = this@toEntity.accountType
        currency = this@toEntity.currency
        balance = this@toEntity.balance
        availableBalance = this@toEntity.availableBalance
        status = this@toEntity.status
        alias = this@toEntity.alias
        overdraftLimit = this@toEntity.overdraftLimit
        createdAt = this@toEntity.createdAt
        updatedAt = this@toEntity.updatedAt
    }
}

fun AccountEntity.toDomain(): AccountModel {
    return AccountModel(
        accountId = idAccount,
        accountNumber = accountNumber,
        userId = userId,
        accountType = accountType,
        currency = currency,
        balance = balance,
        availableBalance = availableBalance,
        status = status,
        alias = alias,
        overdraftLimit = overdraftLimit,
        createdAt = createdAt,
        updatedAt = updatedAt ?: Timestamp.from(Instant.now())
    )
}
