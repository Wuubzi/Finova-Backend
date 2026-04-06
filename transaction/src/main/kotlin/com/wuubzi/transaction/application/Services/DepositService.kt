package com.wuubzi.transaction.application.Services

import com.wuubzi.transaction.application.DTOS.Events.EventType
import com.wuubzi.transaction.application.DTOS.Events.TransactionEvent
import com.wuubzi.transaction.application.DTOS.Events.TransactionStatus
import com.wuubzi.transaction.application.DTOS.Events.TransactionType
import com.wuubzi.transaction.application.Ports.In.DepositUseCase
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
class DepositService(
    private val transactionRepository: TransactionRepositoryPort,
    private val kafka: KafkaPort,
    private val webClient: WebClientPort,
    private val cachePort: CachePort
) : DepositUseCase {

    companion object {
        private const val CACHE_PREFIX_LIST = "transaction:account:"
    }

    override fun deposit(accountNumber: String, amount: Double, email: String) {

        val account = webClient.getAccountByAccountNumber(accountNumber)
            ?: throw IllegalArgumentException("Account with number $accountNumber not found")

        require(account.status == "ACTIVE") { "Account is not active" }

        val transaction = TransactionModel(
            transactionId = UUID.randomUUID(),
            fromAccountId = null,
            toAccountId = account.accountId,
            amount = amount,
            currency = account.currency,
            status = "PENDING",
            type = "DEPOSIT",
            description = "Deposit to account ${account.accountNumber}",
            createdAt = Timestamp.from(Instant.now())
        )

        transactionRepository.save(transaction)
        cachePort.delete("$CACHE_PREFIX_LIST${account.accountId}")

        kafka.publishTransactionEvent(
            TransactionEvent(
                transactionId = transaction.transactionId,
                eventType = EventType.TRANSACTION_CREATED,
                type = TransactionType.DEPOSIT,
                amount = amount,
                toAccountId = account.accountId,
                currency = account.currency,
                status = TransactionStatus.PENDING,
                description = transaction.description,
                email = email
            )
        )
    }
}