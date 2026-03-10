package com.wuubzi.auth.infrastructure.Adapters

import com.wuubzi.auth.application.DTOS.Events.UserCreated
import com.wuubzi.auth.application.Ports.out.KafkaPort
import com.wuubzi.auth.domain.models.UserCredentials
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaAdapter(
    private val kafkaTemplate: KafkaTemplate<String, Any>
): KafkaPort {
    override fun publishUserCreated(user: UserCreated) {
      kafkaTemplate.send("user-created", user)
    }

    override fun publishRecoverPassword(otp: String) {
        kafkaTemplate.send("recover-password", otp)
    }

}