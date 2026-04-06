package com.wuubzi.transaction.application.Ports.Out

import com.wuubzi.transaction.application.DTOS.Events.TransactionEvent

interface KafkaPort {
    fun publishTransactionEvent(event: TransactionEvent)
}