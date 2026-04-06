package com.wuubzi.transaction.application.Services

import com.wuubzi.transaction.application.Ports.In.GetTransactionUseCase
import com.wuubzi.transaction.application.Ports.Out.CachePort
import com.wuubzi.transaction.application.Ports.Out.TransactionRepositoryPort
import com.wuubzi.transaction.domain.Models.TransactionModel
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class GetTransactionService(
    private val transactionRepository: TransactionRepositoryPort,
    private val cachePort: CachePort
): GetTransactionUseCase {

    companion object {
        private const val CACHE_PREFIX = "transaction:"
        private val CACHE_TTL = Duration.ofMinutes(10)
    }

    override fun getTransaction(transactionId: UUID): TransactionModel {
        val cacheKey = "$CACHE_PREFIX$transactionId"
        val cached = cachePort.getObject(cacheKey, TransactionModel::class.java)
        if (cached != null) return cached

        val transaction = transactionRepository.findById(transactionId)
            ?: throw IllegalArgumentException("Transaction with id $transactionId not found")

        cachePort.saveObject(cacheKey, transaction, CACHE_TTL)
        return transaction
    }
}