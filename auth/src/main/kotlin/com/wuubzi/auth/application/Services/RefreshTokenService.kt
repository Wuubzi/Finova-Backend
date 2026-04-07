package com.wuubzi.auth.application.Services

import com.wuubzi.auth.application.DTOS.Request.RefreshTokenRequest
import com.wuubzi.auth.application.Exceptions.TokenExpiredException
import com.wuubzi.auth.application.Exceptions.TokenNotFoundException
import com.wuubzi.auth.application.Exceptions.TokenRevokedException
import com.wuubzi.auth.application.Ports.`in`.RefreshTokenUseCase
import com.wuubzi.auth.application.Ports.out.JwtPort
import com.wuubzi.auth.application.Ports.out.RefreshTokenRepositoryPort
import com.wuubzi.auth.application.Ports.out.UserCredentialsRepositoryPort
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class RefreshTokenService(
   private val refreshTokenRepository: RefreshTokenRepositoryPort,
   private val jwtPort: JwtPort,
   private val userCredentialsRepository: UserCredentialsRepositoryPort
): RefreshTokenUseCase {
    override fun refreshToken(refreshTokenRequest: RefreshTokenRequest): String {
        val refreshTokenEntity = refreshTokenRepository.findByToken(refreshTokenRequest.refreshToken)
            ?: throw TokenNotFoundException("The session token provided is invalid.")

        if (refreshTokenEntity.isRevoked) {
            throw TokenRevokedException("This session has been terminated for security reasons.")
        }

        if (refreshTokenEntity.expiresAt < Instant.now()) {
            throw TokenExpiredException("Your session has expired. Please log in again.")
        }

        val user = userCredentialsRepository.findByUserId(refreshTokenEntity.userId)
            ?: throw IllegalArgumentException("User not found for refresh token")

        val accessToken = jwtPort.generateToken(refreshTokenEntity.userId, user.email)

        return accessToken
    }
}