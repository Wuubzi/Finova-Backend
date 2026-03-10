package com.wuubzi.auth.application.Services

import com.wuubzi.auth.application.Ports.`in`.LogoutUseCase
import com.wuubzi.auth.application.Ports.out.RefreshTokenRepositoryPort
import org.springframework.stereotype.Service

@Service
class LogoutService(
    private val refreshTokenRepository: RefreshTokenRepositoryPort
): LogoutUseCase {
    override fun logout(refreshToken: String) {
        val refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
            ?: throw IllegalArgumentException("Refresh token not found")

        if (refreshTokenEntity.isRevoked) {
            throw IllegalArgumentException("Refresh token already revoked")
        }

        val revokedToken = refreshTokenEntity.copy(
            isRevoked = true
        )
        refreshTokenRepository.save(revokedToken)
    }
}