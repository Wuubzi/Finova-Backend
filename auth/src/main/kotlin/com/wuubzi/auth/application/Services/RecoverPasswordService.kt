package com.wuubzi.auth.application.Services

import com.wuubzi.auth.application.DTOS.Events.RecoverPassword
import com.wuubzi.auth.application.Ports.`in`.RecoverPasswordUseCase
import com.wuubzi.auth.application.Ports.out.CachePort
import com.wuubzi.auth.application.Ports.out.KafkaPort
import com.wuubzi.auth.application.Ports.out.OtpPort
import com.wuubzi.auth.infrastructure.Repositories.UserCredentialsRepository
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RecoverPasswordService(
    private val userCredentialsRepository: UserCredentialsRepository,
    private val otpPort: OtpPort,
    private val kafkaPort: KafkaPort,
    private val cachePort: CachePort,
): RecoverPasswordUseCase {
    override fun recoverPassword(email: String) {
        userCredentialsRepository.findByEmail(email)
            ?: throw IllegalArgumentException("User with email $email not found")

        val otp = otpPort.generateOtp()
        cachePort.save("recover-password-otp:$email", otp, Duration.ofMinutes(10))

        val recoverPasswordEvent = RecoverPassword(
            email = email,
            otp = otp
        )

        kafkaPort.publishRecoverPassword(recoverPasswordEvent)
    }
}