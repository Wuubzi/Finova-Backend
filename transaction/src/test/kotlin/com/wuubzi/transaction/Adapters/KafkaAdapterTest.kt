package com.wuubzi.transaction.Adapters

import com.wuubzi.transaction.application.DTOS.Events.EventType
import com.wuubzi.transaction.application.DTOS.Events.TransactionEvent
import com.wuubzi.transaction.application.DTOS.Events.TransactionStatus
import com.wuubzi.transaction.application.DTOS.Events.TransactionType
import com.wuubzi.transaction.infrastructure.Adapters.KafkaAdapter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.springframework.kafka.core.KafkaTemplate
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class KafkaAdapterTest {

    @Mock
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @InjectMocks
    lateinit var kafkaAdapter: KafkaAdapter

    @Test
    fun shouldPublishTransactionEvent() {
        // GIVEN
        val transactionId = UUID.randomUUID()
        val event = TransactionEvent(
            transactionId = transactionId,
            eventType = EventType.TRANSACTION_CREATED,
            type = TransactionType.DEPOSIT,
            amount = 100.0,
            toAccountId = UUID.randomUUID(),
            currency = "USD",
            status = TransactionStatus.PENDING,
            description = "Test deposit",
            email = "test@email.com"
        )

        // WHEN
        kafkaAdapter.publishTransactionEvent(event)

        // THEN
        verify(kafkaTemplate).send("transactions.events", transactionId.toString(), event)
    }
}

