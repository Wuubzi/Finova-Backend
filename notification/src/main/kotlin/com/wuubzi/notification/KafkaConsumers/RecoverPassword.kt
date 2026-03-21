package com.wuubzi.notification.KafkaConsumers

import com.wuubzi.notification.DTOS.Events.RecoverPasswordRequest
import com.wuubzi.notification.Services.MailService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class RecoverPassword(
    private val mailService: MailService
) {

    @KafkaListener(
        topics = ["recover-password"],
        groupId = "notification-service-v2"
    )
    fun listen(data: RecoverPasswordRequest) {
        try {
            mailService.sendRecoverPasswordEmail(data.email, data.otp)
            println("Email sent successfully to ${data.email}")
        } catch (e: Exception) {
            println("Error sending email: ${e.message}")
            e.printStackTrace()
        }
    }
}