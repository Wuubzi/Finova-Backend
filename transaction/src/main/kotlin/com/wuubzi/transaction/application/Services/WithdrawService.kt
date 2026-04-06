package com.wuubzi.transaction.application.Services

import com.wuubzi.transaction.application.DTOS.Events.EventType
import com.wuubzi.transaction.application.DTOS.Events.TransactionEvent
import com.wuubzi.transaction.application.DTOS.Events.TransactionStatus
import com.wuubzi.transaction.application.DTOS.Events.TransactionType
import com.wuubzi.transaction.application.Ports.In.WithdrawUseCase
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
class WithdrawService(
    private val transactionRepository: TransactionRepositoryPort,
    private val webClient: WebClientPort,
    private val kafkaPort: KafkaPort,
    private val cachePort: CachePort
) : WithdrawUseCase {

    companion object {
        private const val CACHE_PREFIX_LIST = "transaction:account:"
    }

    override fun withdraw(accountNumber: String, amount: Double, email: String) {
        require(amount > 0) { "Withdraw amount must be greater than zero" }

        val account = webClient.getAccountByAccountNumber(accountNumber)
            ?: throw IllegalArgumentException("Account with number $accountNumber not found")

        require(account.status == "ACTIVE") { "Account is not active" }
        require(account.availableBalance >= amount) { "Insufficient balance" }

        val transaction = TransactionModel(
            transactionId = UUID.randomUUID(),
            fromAccountId = account.accountId,
            toAccountId = null,
            amount = amount,
            currency = account.currency,
            status = "PENDING",
            type = "WITHDRAW",
            description = "Withdrawal from account ${account.accountNumber}",
            createdAt = Timestamp.from(Instant.now())
        )

        transactionRepository.save(transaction)
        cachePort.delete("$CACHE_PREFIX_LIST${account.accountId}")

        kafkaPort.publishTransactionEvent(
            TransactionEvent(
                transactionId = transaction.transactionId,
                eventType = EventType.TRANSACTION_CREATED,
                type = TransactionType.WITHDRAW,
                amount = amount,
                fromAccountId = account.accountId,
                currency = account.currency,
                status = TransactionStatus.PENDING,
                description = transaction.description,
                email = email
            )
        )
    }
}