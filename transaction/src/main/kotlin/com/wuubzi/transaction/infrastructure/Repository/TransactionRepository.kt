package com.wuubzi.transaction.infrastructure.Repository

import com.wuubzi.transaction.infrastructure.Persistence.Entities.TransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TransactionRepository: JpaRepository<TransactionEntity, UUID> {
    fun findByFromAccountIdOrToAccountId(fromAccountId: UUID, toAccountId: UUID): List<TransactionEntity>
}