package com.wuubzi.auth.application.Services

import com.wuubzi.auth.application.DTOS.Request.ValidateOTPRequest
import com.wuubzi.auth.application.Exceptions.InvalidOtpException
import com.wuubzi.auth.application.Ports.`in`.ValidateOTPUseCase
import com.wuubzi.auth.application.Ports.out.CachePort
import com.wuubzi.auth.application.Ports.out.UserCredentialsRepositoryPort
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class ValidateOTPService (
    private val userCredentialsRepository: UserCredentialsRepositoryPort,
    private val cachePort: CachePort
): ValidateOTPUseCase {
    override fun validateOTP(validateOtpRequest: ValidateOTPRequest): String {

        userCredentialsRepository.findByEmail(validateOtpRequest.email)
            ?: throw IllegalArgumentException("User with email ${validateOtpRequest.email} not found")

        val otp = cachePort.get("recover-password-otp:${validateOtpRequest.email}")

        if (otp != validateOtpRequest.otp) {
            throw InvalidOtpException("The verification code is incorrect or has expired.")
        }

        val resetToken = UUID.randomUUID().toString()
        cachePort.save("reset-password-token:${validateOtpRequest.email}", resetToken, Duration.ofMinutes(10))
        cachePort.delete("recover-password-otp:${validateOtpRequest.email}")

        return resetToken
    }
}