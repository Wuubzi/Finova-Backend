package com.wuubzi.transaction.application.Services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.wuubzi.transaction.application.Ports.In.GetAllTransactionUseCase
import com.wuubzi.transaction.application.Ports.Out.CachePort
import com.wuubzi.transaction.application.Ports.Out.TransactionRepositoryPort
import com.wuubzi.transaction.application.Ports.Out.WebClientPort
import com.wuubzi.transaction.domain.Models.TransactionModel
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class GetAllTransactionUseCaseService(
    private val transactionRepository: TransactionRepositoryPort,
    private val webClient: WebClientPort,
    private val cachePort: CachePort,
    private val objectMapper: ObjectMapper
): GetAllTransactionUseCase {

    companion object {
        private const val CACHE_PREFIX = "transaction:account:"
        private val CACHE_TTL = Duration.ofMinutes(5)
    }

    override fun getAllTransactions(accountNumber: String): List<TransactionModel> {
        val account = webClient.getAccountByAccountNumber(accountNumber)
            ?: throw IllegalArgumentException("Account with number $accountNumber not found")

        val cacheKey = "$CACHE_PREFIX${account.accountId}"
        val cachedJson = cachePort.get(cacheKey)
        if (cachedJson != null) {
            return try {
                objectMapper.readValue(cachedJson, object : TypeReference<List<TransactionModel>>() {})
            } catch (_: Exception) {
                emptyList()
            }
        }

        val transactions = transactionRepository.findAllByAccountId(account.accountId)
        cachePort.save(cacheKey, objectMapper.writeValueAsString(transactions), CACHE_TTL)
        return transactions
    }
}