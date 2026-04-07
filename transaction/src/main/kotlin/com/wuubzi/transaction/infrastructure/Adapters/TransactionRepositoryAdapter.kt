package com.wuubzi.transaction.infrastructure.Adapters

import com.wuubzi.transaction.application.Ports.Out.TransactionRepositoryPort
import com.wuubzi.transaction.domain.Models.TransactionModel
import com.wuubzi.transaction.infrastructure.Persistence.Mappers.toDomain
import com.wuubzi.transaction.infrastructure.Persistence.Mappers.toEntity
import com.wuubzi.transaction.infrastructure.Repository.TransactionRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class TransactionRepositoryAdapter(
    private val transactionRepository: TransactionRepository
): TransactionRepositoryPort {
    override fun save(transaction: TransactionModel): TransactionModel =
        transactionRepository.save(transaction.toEntity()).toDomain()

    override fun findById(transactionId: UUID): TransactionModel? =
        transactionRepository.findById(transactionId).orElse(null)?.toDomain()

    override fun findAllByAccountId(accountId: UUID): List<TransactionModel> =
        transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId).map { it.toDomain() }

    override fun updateStatus(transactionId: UUID, status: String) {
        val entity = transactionRepository.findById(transactionId)
            .orElseThrow { IllegalArgumentException("Transaction with id $transactionId not found") }
        entity.status = status
        transactionRepository.save(entity)
    }
}