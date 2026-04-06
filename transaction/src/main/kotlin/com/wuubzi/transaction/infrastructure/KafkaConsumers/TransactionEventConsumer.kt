package com.wuubzi.transaction.infrastructure.KafkaConsumers

import com.wuubzi.transaction.application.DTOS.Events.EventType
import com.wuubzi.transaction.application.DTOS.Events.TransactionEvent
import com.wuubzi.transaction.application.Ports.Out.CachePort
import com.wuubzi.transaction.application.Ports.Out.TransactionRepositoryPort
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class TransactionEventConsumer(
    private val transactionRepository: TransactionRepositoryPort,
    private val cachePort: CachePort
) {

    companion object {
        private const val CACHE_PREFIX = "transaction:"
        private const val CACHE_PREFIX_LIST = "transaction:account:"
    }

    @KafkaListener(
        topics = ["transactions.events"],
        groupId = "transaction-service",
        containerFactory = "transactionEventKafkaListenerContainerFactory"
    )
    fun listen(event: TransactionEvent) {
        when (event.eventType) {
            EventType.TRANSACTION_COMPLETED -> {
                transactionRepository.updateStatus(event.transactionId, "COMPLETED")
                invalidateCache(event)
                println("[Transaction Service] Transaction ${event.transactionId} marked as COMPLETED")
            }
            EventType.TRANSACTION_FAILED -> {
                transactionRepository.updateStatus(event.transactionId, "FAILED")
                invalidateCache(event)
                println("[Transaction Service] Transaction ${event.transactionId} marked as FAILED")
            }
            else -> { /* Ignore TRANSACTION_CREATED — this service published it */ }
        }
    }

    private fun invalidateCache(event: TransactionEvent) {
        cachePort.delete("$CACHE_PREFIX${event.transactionId}")
        event.fromAccountId?.let { cachePort.delete("$CACHE_PREFIX_LIST$it") }
        event.toAccountId?.let { cachePort.delete("$CACHE_PREFIX_LIST$it") }
    }
}

