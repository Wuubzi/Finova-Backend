package com.wuubzi.auth.application.Services

import com.wuubzi.auth.application.DTOS.Request.ChangePasswordRequest
import com.wuubzi.auth.application.Ports.`in`.ChangePasswordUseCase
import com.wuubzi.auth.application.Ports.out.CachePort
import com.wuubzi.auth.infrastructure.Repositories.UserCredentialsRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class ChangePasswordService(
    private val userCredentialsRepository: UserCredentialsRepository,
    private val cachePort: CachePort,
    private val passwordEncoder: PasswordEncoder
): ChangePasswordUseCase {
    override fun changePassword(changePasswordRequest: ChangePasswordRequest) {
        val user = userCredentialsRepository.findByEmail(changePasswordRequest.email)
            ?: throw IllegalArgumentException("User with email ${changePasswordRequest.email} not found")

        val canChangePassword = cachePort.get("reset-password-token:${changePasswordRequest.email}")

        require(canChangePassword == changePasswordRequest.resetToken) {
            throw IllegalArgumentException("Invalid or expired reset token")
        }

        require(changePasswordRequest.password == changePasswordRequest.confirmPassword) {
            throw IllegalArgumentException("Passwords do not match")
        }

        val password = passwordEncoder.encode(changePasswordRequest.password)

        user.password = (password)
        userCredentialsRepository.save(user)
    }
}