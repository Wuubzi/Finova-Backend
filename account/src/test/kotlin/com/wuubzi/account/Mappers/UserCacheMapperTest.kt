package com.wuubzi.account.Mappers

import com.wuubzi.account.domain.models.UserCachedModel
import com.wuubzi.account.infrastructure.Persistence.Entities.UserCacheEntity
import com.wuubzi.account.infrastructure.Persistence.Mappers.toDomain
import com.wuubzi.account.infrastructure.Persistence.Mappers.toEntity
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class UserCacheMapperTest {

    private val userId = UUID.randomUUID()

    @Test
    fun shouldMapModelToEntity() {
        // GIVEN
        val model = UserCachedModel(userId = userId)

        // WHEN
        val entity = model.toEntity()

        // THEN
        assertEquals(userId, entity.userId)
    }

    @Test
    fun shouldMapEntityToDomain() {
        // GIVEN
        val entity = UserCacheEntity().apply {
            userId = this@UserCacheMapperTest.userId
        }

        // WHEN
        val model = entity.toDomain()

        // THEN
        assertEquals(userId, model.userId)
    }
}

