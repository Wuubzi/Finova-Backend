package com.wuubzi.transaction.application.Services

import com.wuubzi.transaction.application.DTOS.Events.EventType
import com.wuubzi.transaction.application.DTOS.Events.TransactionEvent
import com.wuubzi.transaction.application.DTOS.Events.TransactionStatus
import com.wuubzi.transaction.application.DTOS.Events.TransactionType
import com.wuubzi.transaction.application.Ports.In.TransferUseCase
import com.wuubzi.transaction.application.Ports.Out.CachePort
import com.wuubzi.transaction.application.Ports.Out.KafkaPort
import com.wuubzi.transaction.application.Ports.Out.TransactionRepositoryPort
import com.wuubzi.transaction.application.Ports.Out.WebClientPort
import com.wuubzi.transaction.domain.Models.TransactionModel
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

@Service
class TransferService(
    private val transactionRepository: TransactionRepositoryPort,
    private val webClient: WebClientPort,
    private val kafkaPort: KafkaPort,
    private val cachePort: CachePort
) : TransferUseCase {

    companion object {
        private const val CACHE_PREFIX_LIST = "transaction:account:"
    }

    override fun transfer(fromAccountNumber: String, toAccountNumber: String, amount: Double, email: String) {
        require(fromAccountNumber != toAccountNumber) { "Cannot transfer to the same account" }

        val fromAccount = webClient.getAccountByAccountNumber(fromAccountNumber)
            ?: throw IllegalArgumentException("Source account with number $fromAccountNumber not found")

        val toAccount = webClient.getAccountByAccountNumber(toAccountNumber)
            ?: throw IllegalArgumentException("Destination account with number $toAccountNumber not found")

        require(fromAccount.status == "ACTIVE") { "Source account is not active" }
        require(toAccount.status == "ACTIVE") { "Destination account is not active" }
        require(fromAccount.currency == toAccount.currency) { "Currency mismatch between accounts" }
        require(fromAccount.availableBalance >= amount) { "Insufficient balance" }

        val transaction = TransactionModel(
            transactionId = UUID.randomUUID(),
            fromAccountId = fromAccount.accountId,
            toAccountId = toAccount.accountId,
            amount = amount,
            currency = fromAccount.currency,
            status = "PENDING",
            type = "TRANSFER",
            description = "Transfer from ${fromAccount.accountNumber} to ${toAccount.accountNumber}",
            createdAt = Timestamp.from(Instant.now())
        )

        transactionRepository.save(transaction)
        cachePort.delete("$CACHE_PREFIX_LIST${fromAccount.accountId}")
        cachePort.delete("$CACHE_PREFIX_LIST${toAccount.accountId}")

        kafkaPort.publishTransactionEvent(
            TransactionEvent(
                transactionId = transaction.transactionId,
                eventType = EventType.TRANSACTION_CREATED,
                type = TransactionType.TRANSFER,
                amount = amount,
                fromAccountId = fromAccount.accountId,
                toAccountId = toAccount.accountId,
                currency = fromAccount.currency,
                status = TransactionStatus.PENDING,
                description = transaction.description,
                email = email
            )
        )
    }
}