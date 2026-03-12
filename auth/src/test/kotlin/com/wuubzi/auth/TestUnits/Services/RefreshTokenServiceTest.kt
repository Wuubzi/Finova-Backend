package com.wuubzi.auth.TestUnits.Services

import com.wuubzi.auth.application.DTOS.Request.RefreshTokenRequest
import com.wuubzi.auth.application.Exceptions.TokenExpiredException
import com.wuubzi.auth.application.Exceptions.TokenNotFoundException
import com.wuubzi.auth.application.Exceptions.TokenRevokedException
import com.wuubzi.auth.application.Ports.out.JwtPort
import com.wuubzi.auth.application.Ports.out.RefreshTokenRepositoryPort
import com.wuubzi.auth.application.Services.RefreshTokenService
import com.wuubzi.auth.domain.models.RefreshToken
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class RefreshTokenServiceTest {

    @Mock
    lateinit var refreshTokenRepository: RefreshTokenRepositoryPort

    @Mock
    lateinit var jwtPort: JwtPort

    @InjectMocks
    lateinit var refreshTokenService: RefreshTokenService

    private val refreshTokenStr = "valid-refresh-token-xyz"
    private val userId = UUID.randomUUID()

    @Test
    fun shouldRefreshTokenSuccessfully() {
        // GIVEN
        val request = RefreshTokenRequest(refreshTokenStr)
        val refreshTokenEntity = RefreshToken(
            id = UUID.randomUUID(),
            userId = userId,
            token = refreshTokenStr,
            expiresAt = Instant.now().plusSeconds(3600), // Expira en una hora
            isRevoked = false,
            createdAt = Instant.now()
        )

        whenever(refreshTokenRepository.findByToken(refreshTokenStr)).thenReturn(refreshTokenEntity)
        whenever(jwtPort.generateToken(userId)).thenReturn("new-access-token")

        // WHEN
        val result = refreshTokenService.refreshToken(request)

        // THEN
        assertEquals("new-access-token", result)
        verify(jwtPort).generateToken(userId)
    }

    @Test
    fun shouldThrowTokenNotFoundExceptionWhenTokenDoesNotExist() {
        // GIVEN
        val request = RefreshTokenRequest("non-existent-token")
        whenever(refreshTokenRepository.findByToken(any())).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows(TokenNotFoundException::class.java) {
            refreshTokenService.refreshToken(request)
        }
        assertEquals("The session token provided is invalid.", exception.message)
    }

    @Test
    fun shouldThrowTokenRevokedExceptionWhenTokenIsRevoked() {
        // GIVEN
        val request = RefreshTokenRequest(refreshTokenStr)
        val revokedToken = RefreshToken(
            id = UUID.randomUUID(),
            userId = userId,
            token = refreshTokenStr,
            expiresAt = Instant.now().plusSeconds(3600),
            isRevoked = true, // Token revocado
            createdAt = Instant.now()
        )

        whenever(refreshTokenRepository.findByToken(refreshTokenStr)).thenReturn(revokedToken)

        // WHEN & THEN
        val exception = assertThrows(TokenRevokedException::class.java) {
            refreshTokenService.refreshToken(request)
        }
        assertEquals("This session has been terminated for security reasons.", exception.message)
        verify(jwtPort, never()).generateToken(any())
    }

    @Test
    fun shouldThrowTokenExpiredExceptionWhenTokenIsExpired() {
        // GIVEN
        val request = RefreshTokenRequest(refreshTokenStr)
        val expiredToken = RefreshToken(
            id = UUID.randomUUID(),
            userId = userId,
            token = refreshTokenStr,
            expiresAt = Instant.now().minusSeconds(60), // Expiró hace un minuto
            isRevoked = false,
            createdAt = Instant.now().minusSeconds(7200)
        )

        whenever(refreshTokenRepository.findByToken(refreshTokenStr)).thenReturn(expiredToken)

        // WHEN & THEN
        val exception = assertThrows(TokenExpiredException::class.java) {
            refreshTokenService.refreshToken(request)
        }
        assertEquals("Your session has expired. Please log in again.", exception.message)
        verify(jwtPort, never()).generateToken(any())
    }
}