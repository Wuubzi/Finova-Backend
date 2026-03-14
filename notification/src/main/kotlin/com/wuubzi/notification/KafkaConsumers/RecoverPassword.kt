package com.wuubzi.notification.KafkaConsumers

import com.wuubzi.notification.Services.MailService
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class RecoverPassword(
    private val mailService: MailService
) {

    @KafkaListener(topics = ["recover-password"], groupId = "notification-service")
    fun listen(data: String) {
        mailService.sendMail()
        println("Received data: $data")
    }
}