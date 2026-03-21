package com.wuubzi.user.infrastructure.Adapters

import com.wuubzi.user.application.DTOS.Events.UserDeletedEvent
import com.wuubzi.user.application.Ports.Out.KafkaPort
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaAdapter(
    private val kafkaTemplate: KafkaTemplate<String, Any>
): KafkaPort {
    override fun publishUserDeleted(user: UserDeletedEvent) {
        kafkaTemplate.send("user-deleted", user)
    }
}