package com.wuubzi.notification.KafkaConsumers

import com.wuubzi.notification.DTOS.Events.EventType
import com.wuubzi.notification.DTOS.Events.TransactionEvent
import com.wuubzi.notification.DTOS.Events.TransactionType
import com.wuubzi.notification.Services.MailService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class TransactionEventConsumer(
    private val mailService: MailService
) {

    @KafkaListener(
        topics = ["transactions.events"],
        groupId = "notification-service",
        containerFactory = "transactionEventKafkaListenerContainerFactory"
    )
    fun listen(event: TransactionEvent) {
        val transactionLabel = when (event.type) {
            TransactionType.DEPOSIT -> "Deposit"
            TransactionType.WITHDRAW -> "Withdrawal"
            TransactionType.TRANSFER -> "Transfer"
        }

        val message = when (event.eventType) {
            EventType.TRANSACTION_CREATED ->
                "[$transactionLabel] Transaction ${event.transactionId} in progress — Amount: ${event.amount} ${event.currency}"
            EventType.TRANSACTION_COMPLETED ->
                "[$transactionLabel] Transaction ${event.transactionId} completed successfully — Amount: ${event.amount} ${event.currency}"
            EventType.TRANSACTION_FAILED ->
                "[$transactionLabel] Transaction ${event.transactionId} failed — Amount: ${event.amount} ${event.currency}"
        }

        println("[Notification Service] $message")

        if (event.email != null) {
            try {
                mailService.sendTransactionEmail(
                    email = event.email,
                    transactionId = event.transactionId.toString(),
                    type = event.type.name,
                    eventType = event.eventType.name,
                    amount = event.amount,
                    currency = event.currency,
                    description = event.description
                )
                println("[Notification Service] Email sent to ${event.email}")
            } catch (e: Exception) {
                println("[Notification Service] Failed to send email to ${event.email}: ${e.message}")
            }
        }
    }
}
