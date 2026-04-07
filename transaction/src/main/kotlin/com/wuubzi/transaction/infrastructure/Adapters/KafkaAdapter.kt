package com.wuubzi.transaction.infrastructure.Adapters

import com.wuubzi.transaction.application.DTOS.Events.TransactionEvent
import com.wuubzi.transaction.application.Ports.Out.KafkaPort
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaAdapter(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) : KafkaPort {

    companion object {
        private const val TOPIC = "transactions.events"
    }

    override fun publishTransactionEvent(event: TransactionEvent) {
        kafkaTemplate.send(TOPIC, event.transactionId.toString(), event)
    }
}