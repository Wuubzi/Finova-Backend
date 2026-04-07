package com.wuubzi.transaction.application.Ports.Out

import com.wuubzi.transaction.application.DTOS.Events.TransactionEvent

fun interface KafkaPort {
    fun publishTransactionEvent(event: TransactionEvent)
}