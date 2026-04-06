package com.wuubzi.account.infrastructure.KafkaConsumers

import com.wuubzi.account.application.DTOS.Events.EventType
import com.wuubzi.account.application.DTOS.Events.TransactionEvent
import com.wuubzi.account.application.DTOS.Events.TransactionStatus
import com.wuubzi.account.application.DTOS.Events.TransactionType
import com.wuubzi.account.application.Ports.out.CachePort
import com.wuubzi.account.infrastructure.Persistence.Entities.AccountEntity
import com.wuubzi.account.infrastructure.Repositories.AccountRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.Instant

@Component
class TransactionEventConsumer(
    private val accountRepository: AccountRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val cachePort: CachePort
) {

    companion object {
        private const val TOPIC = "transactions.events"
        private const val CACHE_PREFIX_USER = "account:user:"
        private const val CACHE_PREFIX_NUMBER = "account:number:"
    }

    @KafkaListener(
        topics = [TOPIC],
        groupId = "account-service",
        containerFactory = "transactionEventKafkaListenerContainerFactory"
    )
    fun listen(event: TransactionEvent) {
        if (event.eventType != EventType.TRANSACTION_CREATED) return

        try {
            when (event.type) {
                TransactionType.DEPOSIT -> processDeposit(event)
                TransactionType.WITHDRAW -> processWithdraw(event)
                TransactionType.TRANSFER -> processTransfer(event)
            }

            publishResult(event, EventType.TRANSACTION_COMPLETED, TransactionStatus.COMPLETED)
            println("[Account Service] Transaction ${event.transactionId} processed successfully (${event.type})")

        } catch (e: Exception) {
            publishResult(event, EventType.TRANSACTION_FAILED, TransactionStatus.FAILED)
            println("[Account Service] Transaction ${event.transactionId} failed: ${e.message}")
        }
    }

    private fun processDeposit(event: TransactionEvent) {
        val accountId = event.toAccountId
            ?: throw IllegalArgumentException("toAccountId is required for DEPOSIT")

        val account = accountRepository.findById(accountId)
            .orElseThrow { IllegalArgumentException("Account with id $accountId not found") }

        account.balance += event.amount
        account.availableBalance += event.amount
        account.updatedAt = Timestamp.from(Instant.now())

        accountRepository.save(account)
        invalidateAccountCache(account)
    }

    private fun processWithdraw(event: TransactionEvent) {
        val accountId = event.fromAccountId
            ?: throw IllegalArgumentException("fromAccountId is required for WITHDRAW")

        val account = accountRepository.findById(accountId)
            .orElseThrow { IllegalArgumentException("Account with id $accountId not found") }

        require(account.availableBalance >= event.amount) { "Insufficient balance" }

        account.balance -= event.amount
        account.availableBalance -= event.amount
        account.updatedAt = Timestamp.from(Instant.now())

        accountRepository.save(account)
        invalidateAccountCache(account)
    }

    private fun processTransfer(event: TransactionEvent) {
        val fromAccountId = event.fromAccountId
            ?: throw IllegalArgumentException("fromAccountId is required for TRANSFER")
        val toAccountId = event.toAccountId
            ?: throw IllegalArgumentException("toAccountId is required for TRANSFER")

        val fromAccount = accountRepository.findById(fromAccountId)
            .orElseThrow { IllegalArgumentException("Source account with id $fromAccountId not found") }
        val toAccount = accountRepository.findById(toAccountId)
            .orElseThrow { IllegalArgumentException("Destination account with id $toAccountId not found") }

        require(fromAccount.availableBalance >= event.amount) { "Insufficient balance in source account" }

        val now = Timestamp.from(Instant.now())

        fromAccount.balance -= event.amount
        fromAccount.availableBalance -= event.amount
        fromAccount.updatedAt = now

        toAccount.balance += event.amount
        toAccount.availableBalance += event.amount
        toAccount.updatedAt = now

        accountRepository.save(fromAccount)
        accountRepository.save(toAccount)
        invalidateAccountCache(fromAccount)
        invalidateAccountCache(toAccount)
    }

    private fun invalidateAccountCache(account: AccountEntity) {
        cachePort.delete("$CACHE_PREFIX_USER${account.userId}")
        cachePort.delete("$CACHE_PREFIX_NUMBER${account.accountNumber}")
    }

    private fun publishResult(original: TransactionEvent, eventType: EventType, status: TransactionStatus) {
        val resultEvent = original.copy(
            eventType = eventType,
            status = status,
            timestamp = Instant.now()
        )
        kafkaTemplate.send(TOPIC, original.transactionId.toString(), resultEvent)
    }
}

