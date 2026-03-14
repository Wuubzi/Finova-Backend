package com.wuubzi.auth.Mappers

import com.wuubzi.auth.domain.models.RefreshToken
import com.wuubzi.auth.infrastructure.Persistence.Entities.RefreshTokenEntity
import com.wuubzi.auth.infrastructure.Persistence.Mappers.toDomain
import com.wuubzi.auth.infrastructure.Persistence.Mappers.toEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class RefreshTokenMapperTest {

    @Test
    fun shouldMapRefreshTokenToEntity() {
        // GIVEN
        val domain = RefreshToken(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            token = "sample-refresh-token",
            expiresAt = Instant.now().plusSeconds(3600),
            isRevoked = false,
            createdAt = Instant.now()
        )

        // WHEN
        val entity = domain.toEntity()

        // THEN
        assertEquals(domain.id, entity.id)
        assertEquals(domain.userId, entity.userId)
        assertEquals(domain.token, entity.token)
        assertEquals(domain.expiresAt, entity.expiresAt)
        assertEquals(domain.isRevoked, entity.isRevoked)
        assertEquals(domain.createdAt, entity.createdAt)
    }

    @Test
    fun shouldMapRefreshTokenEntityToDomain() {
        // GIVEN
        val entity = RefreshTokenEntity().apply {
            id = UUID.randomUUID()
            userId = UUID.randomUUID()
            token = "entity-token-123"
            expiresAt = Instant.now().plusSeconds(7200)
            isRevoked = true
            createdAt = Instant.now()
        }

        // WHEN
        val domain = entity.toDomain()

        // THEN
        assertEquals(entity.id, domain.id)
        assertEquals(entity.userId, domain.userId)
        assertEquals(entity.token, domain.token)
        assertEquals(entity.expiresAt, domain.expiresAt)
        assertEquals(entity.isRevoked, domain.isRevoked)
        assertEquals(entity.createdAt, domain.createdAt)
    }
}