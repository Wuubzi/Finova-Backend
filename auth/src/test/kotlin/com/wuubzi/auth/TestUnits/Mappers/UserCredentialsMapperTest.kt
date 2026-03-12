package com.wuubzi.auth.TestUnits.Mappers

import com.wuubzi.auth.domain.models.UserCredentials
import com.wuubzi.auth.infrastructure.Persistence.Entities.UserCredentialsEntity
import com.wuubzi.auth.infrastructure.Persistence.Mappers.toDomain
import com.wuubzi.auth.infrastructure.Persistence.Mappers.toEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.util.UUID

class UserCredentialsMapperTest {
    @Test
    fun shouldMapDomainToEntity() {
        // GIVEN
        val domain = UserCredentials(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            email = "test@wuubzi.com",
            password = "hashedPassword",
            role = "USER",
            isActive = true,
            createdAt = Timestamp(System.currentTimeMillis())
        )

        // WHEN
        val entity = domain.toEntity()

        // THEN
        assertEquals(domain.id, entity.id)
        assertEquals(domain.userId, entity.userId)
        assertEquals(domain.email, entity.email)
        assertEquals(domain.password, entity.password)
        assertEquals(domain.role, entity.role)
        assertEquals(domain.isActive, entity.isActive)
        assertEquals(domain.createdAt, entity.createdAt)
    }

    @Test
    fun shouldMapEntityToDomain() {
        // GIVEN
        val entity = UserCredentialsEntity().apply {
            id = UUID.randomUUID()
            userId = UUID.randomUUID()
            email = "test@wuubzi.com"
            password = "hashedPassword"
            role = "ADMIN"
            isActive = true
            createdAt = Timestamp(System.currentTimeMillis())
        }

        // WHEN
        val domain = entity.toDomain()

        // THEN
        assertEquals(entity.id, domain.id)
        assertEquals(entity.userId, domain.userId)
        assertEquals(entity.email, domain.email)
        assertEquals(entity.password, domain.password)
        assertEquals(entity.role, domain.role)
        assertEquals(entity.isActive, domain.isActive)
        assertEquals(entity.createdAt, domain.createdAt)
    }
}