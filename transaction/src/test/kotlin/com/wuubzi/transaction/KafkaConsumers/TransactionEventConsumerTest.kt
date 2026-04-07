package com.wuubzi.transaction.KafkaConsumers

import com.wuubzi.transaction.application.DTOS.Events.EventType
import com.wuubzi.transaction.application.DTOS.Events.TransactionEvent
import com.wuubzi.transaction.application.DTOS.Events.TransactionStatus
import com.wuubzi.transaction.application.DTOS.Events.TransactionType
import com.wuubzi.transaction.application.Ports.Out.CachePort
import com.wuubzi.transaction.application.Ports.Out.TransactionRepositoryPort
import com.wuubzi.transaction.infrastructure.KafkaConsumers.TransactionEventConsumer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class TransactionEventConsumerTest {

    @Mock
    lateinit var transactionRepository: TransactionRepositoryPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var consumer: TransactionEventConsumer

    private val transactionId = UUID.randomUUID()
    private val fromAccountId = UUID.randomUUID()
    private val toAccountId = UUID.randomUUID()

    private fun mockEvent(eventType: EventType) = TransactionEvent(
        transactionId = transactionId,
        eventType = eventType,
        type = TransactionType.TRANSFER,
        amount = 100.0,
        fromAccountId = fromAccountId,
        toAccountId = toAccountId,
        currency = "USD",
        status = TransactionStatus.PENDING,
        description = "Test transfer",
        email = "test@email.com"
    )

    @Test
    fun shouldUpdateStatusToCompletedAndInvalidateCache() {
        // GIVEN
        val event = mockEvent(EventType.TRANSACTION_COMPLETED)

        // WHEN
        consumer.listen(event)

        // THEN
        verify(transactionRepository).updateStatus(transactionId, "COMPLETED")
        verify(cachePort).delete("transaction:$transactionId")
        verify(cachePort).delete("transaction:account:$fromAccountId")
        verify(cachePort).delete("transaction:account:$toAccountId")
    }

    @Test
    fun shouldUpdateStatusToFailedAndInvalidateCache() {
        // GIVEN
        val event = mockEvent(EventType.TRANSACTION_FAILED)

        // WHEN
        consumer.listen(event)

        // THEN
        verify(transactionRepository).updateStatus(transactionId, "FAILED")
        verify(cachePort).delete("transaction:$transactionId")
        verify(cachePort).delete("transaction:account:$fromAccountId")
        verify(cachePort).delete("transaction:account:$toAccountId")
    }

    @Test
    fun shouldIgnoreTransactionCreatedEvent() {
        // GIVEN
        val event = mockEvent(EventType.TRANSACTION_CREATED)

        // WHEN
        consumer.listen(event)

        // THEN
        verify(transactionRepository, never()).updateStatus(transactionId, "COMPLETED")
        verify(transactionRepository, never()).updateStatus(transactionId, "FAILED")
        verify(cachePort, never()).delete("transaction:$transactionId")
    }

    @Test
    fun shouldHandleEventWithNullAccountIds() {
        // GIVEN
        val event = TransactionEvent(
            transactionId = transactionId,
            eventType = EventType.TRANSACTION_COMPLETED,
            type = TransactionType.DEPOSIT,
            amount = 50.0,
            fromAccountId = null,
            toAccountId = toAccountId,
            currency = "USD",
            status = TransactionStatus.COMPLETED,
            email = "test@email.com"
        )

        // WHEN
        consumer.listen(event)

        // THEN
        verify(transactionRepository).updateStatus(transactionId, "COMPLETED")
        verify(cachePort).delete("transaction:$transactionId")
        verify(cachePort).delete("transaction:account:$toAccountId")
        // fromAccountId is null so no delete for it
    }
}

