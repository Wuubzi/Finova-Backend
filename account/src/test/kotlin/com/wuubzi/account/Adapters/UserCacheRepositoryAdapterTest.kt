package com.wuubzi.account.infrastructure.Adapters

import com.wuubzi.account.infrastructure.Persistence.Entities.UserCacheEntity
import com.wuubzi.account.infrastructure.Repositories.UserCacheRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UserCacheRepositoryAdapterTest {

    @Mock
    lateinit var userCacheRepository: UserCacheRepository

    @InjectMocks
    lateinit var adapter: UserCacheRepositoryAdapter

    private val testUserId = UUID.randomUUID()

    @Test
    fun shouldFindByUserIdSuccessfully() {
        // GIVEN
        val entity = UserCacheEntity()
        // Usamos el nombre exacto del campo: userId
        entity.userId = testUserId

        whenever(userCacheRepository.findByUserId(testUserId)).thenReturn(entity)

        // WHEN
        val result = adapter.findByUserId(testUserId)

        // THEN
        // Verificamos que el mapeo use el campo correcto
        assertEquals(testUserId, result?.userId)
        verify(userCacheRepository).findByUserId(testUserId)
    }

    @Test
    fun shouldReturnNullWhenUserNotInCache() {
        // GIVEN
        whenever(userCacheRepository.findByUserId(testUserId)).thenReturn(null)

        // WHEN
        val result = adapter.findByUserId(testUserId)

        // THEN
        assertNull(result)
        verify(userCacheRepository).findByUserId(testUserId)
    }
}