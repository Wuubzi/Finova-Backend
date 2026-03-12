package com.wuubzi.auth.TestUnits.Services

import com.wuubzi.auth.application.Ports.out.RefreshTokenRepositoryPort
import com.wuubzi.auth.application.Services.LogoutService
import com.wuubzi.auth.domain.models.RefreshToken
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class LogoutServiceTest {

    @Mock
    lateinit var refreshTokenRepository: RefreshTokenRepositoryPort

    @InjectMocks
    lateinit var logoutService: LogoutService

    private val tokenStr = "valid-refresh-token-123"

    @Test
    fun shouldLogoutSuccessfully() {
        // GIVEN
        val refreshToken = RefreshToken(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            token = tokenStr,
            expiresAt = Instant.now().plusSeconds(3600),
            isRevoked = false,
            createdAt = Instant.now()
        )

        whenever(refreshTokenRepository.findByToken(tokenStr)).thenReturn(refreshToken)

        // WHEN
        logoutService.logout(tokenStr)

        // THEN
        val tokenCaptor = argumentCaptor<RefreshToken>()
        verify(refreshTokenRepository).save(tokenCaptor.capture())

        // Verificamos que el token guardado tenga isRevoked = true
        assertEquals(true, tokenCaptor.firstValue.isRevoked)
    }

    @Test
    fun shouldThrowExceptionWhenTokenNotFound() {
        // GIVEN
        whenever(refreshTokenRepository.findByToken(tokenStr)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows(IllegalArgumentException::class.java) {
            logoutService.logout(tokenStr)
        }
        assertEquals("Refresh token not found", exception.message)
    }

    @Test
    fun shouldThrowExceptionWhenTokenIsAlreadyRevoked() {
        // GIVEN
        val revokedToken = RefreshToken(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            token = tokenStr,
            expiresAt = Instant.now().plusSeconds(3600),
            isRevoked = true, // Ya está revocado
            createdAt = Instant.now()
        )

        whenever(refreshTokenRepository.findByToken(tokenStr)).thenReturn(revokedToken)

        // WHEN & THEN
        // Nota: Esto fallará si no corriges el require en el Service a: require(!refreshTokenEntity.isRevoked)
        val exception = assertThrows(IllegalArgumentException::class.java) {
            logoutService.logout(tokenStr)
        }
        assertEquals("Refresh token already revoked", exception.message)
    }
}