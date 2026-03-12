package com.wuubzi.auth.TestUnits.Adapters

import com.wuubzi.auth.domain.models.RefreshToken
import com.wuubzi.auth.infrastructure.Adapters.RefreshTokenRepositoryAdapter
import com.wuubzi.auth.infrastructure.Persistence.Entities.RefreshTokenEntity
import com.wuubzi.auth.infrastructure.Repositories.RefreshTokenRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class RefreshTokenRepositoryAdapterTest {

    @Mock
    lateinit var refreshTokenRepository: RefreshTokenRepository

    @InjectMocks
    lateinit var adapter: RefreshTokenRepositoryAdapter

    @Test
    fun shouldSaveRefreshToken() {
        // GIVEN
        val domainToken = RefreshToken(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            token = "abc-123",
            expiresAt = Instant.now(),
            isRevoked = false,
            createdAt = Instant.now()
        )

        // Mockeamos la entidad que devolvería el repo real
        val entity = RefreshTokenEntity().apply {
            id = domainToken.id
            userId = domainToken.userId
            token = domainToken.token
            expiresAt = domainToken.expiresAt
            isRevoked = domainToken.isRevoked
            createdAt = domainToken.createdAt
        }

        whenever(refreshTokenRepository.save(any())).thenReturn(entity)

        // WHEN
        val result = adapter.save(domainToken)

        // THEN
        assertEquals(domainToken.token, result.token)
        verify(refreshTokenRepository).save(any())
    }

    @Test
    fun shouldFindByTokenSuccessfully() {
        // GIVEN
        val tokenStr = "valid-token"
        val entity = RefreshTokenEntity().apply {
            id = UUID.randomUUID()
            userId = UUID.randomUUID()
            token = tokenStr
            expiresAt = Instant.now()
            isRevoked = false
        }

        whenever(refreshTokenRepository.findByToken(tokenStr)).thenReturn(entity)

        // WHEN
        val result = adapter.findByToken(tokenStr)

        // THEN
        assertEquals(tokenStr, result?.token)
        verify(refreshTokenRepository).findByToken(tokenStr)
    }

    @Test
    fun shouldReturnNullWhenTokenNotFound() {
        // GIVEN
        val tokenStr = "non-existent"
        whenever(refreshTokenRepository.findByToken(tokenStr)).thenReturn(null)

        // WHEN
        val result = adapter.findByToken(tokenStr)

        // THEN
        assertNull(result)
        verify(refreshTokenRepository).findByToken(tokenStr)
    }
}