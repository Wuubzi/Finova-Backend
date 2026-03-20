package com.wuubzi.auth.application.Services

import com.wuubzi.auth.application.DTOS.Request.LoginRequest
import com.wuubzi.auth.application.DTOS.Response.TokenResponse
import com.wuubzi.auth.application.Ports.`in`.LoginUserUseCase
import com.wuubzi.auth.application.Ports.out.JwtPort
import com.wuubzi.auth.application.Ports.out.PasswordEncoderPort
import com.wuubzi.auth.application.Ports.out.RefreshTokenRepositoryPort
import com.wuubzi.auth.application.Ports.out.UserCredentialsRepositoryPort
import com.wuubzi.auth.domain.models.RefreshToken
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

private const val EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000

@Service
class LoginUserService(
    private val userCredentialsRepository: UserCredentialsRepositoryPort,
    private val passwordEncoder: PasswordEncoderPort,
    private val jwtPort: JwtPort,
    private val refreshTokenRepositoryPort: RefreshTokenRepositoryPort
): LoginUserUseCase {

    override fun login(userLogin: LoginRequest): TokenResponse {
       val user = userCredentialsRepository.findByEmail(userLogin.email)
            ?: throw IllegalArgumentException("User with email ${userLogin.email} not found")

        if (!user.isActive) {
            throw IllegalArgumentException("User with email ${userLogin.email} is inactive")
        }

        if (passwordEncoder.matches(userLogin.password, user.password)) {
            val refreshToken = jwtPort.generateRefreshToken()
            val expiresAt = Instant.now().plusMillis(EXPIRATION_TIME.toLong())

            val existsRefreshToken = refreshTokenRepositoryPort.findByUserId(user.userId)
            if (existsRefreshToken != null) {
                val updateToken = existsRefreshToken.copy(
                    token = refreshToken,
                    expiresAt = expiresAt,
                    isRevoked = false,
                    createdAt = Instant.now()
                )
                refreshTokenRepositoryPort.save(updateToken)
            } else {
                val refreshTokenSave  = RefreshToken(
                    id = UUID.randomUUID(),
                    userId = user.userId,
                    token = refreshToken,
                    expiresAt = expiresAt,
                    isRevoked = false,
                    createdAt = Instant.now(),
                )
                refreshTokenRepositoryPort.save(refreshTokenSave)
            }
            return TokenResponse(

                accessToken = jwtPort.generateToken(user.userId),
                refreshToken = refreshToken
            )
        } else {
            throw IllegalArgumentException("Invalid password")
        }
    }


}