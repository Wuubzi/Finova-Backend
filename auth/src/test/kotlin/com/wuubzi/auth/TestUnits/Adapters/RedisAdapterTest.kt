package com.wuubzi.auth.TestUnits.Adapters

import com.wuubzi.auth.infrastructure.Adapters.RedisAdapter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

@ExtendWith(MockitoExtension::class)
class RedisAdapterTest {

    @Mock
    lateinit var redisTemplate: RedisTemplate<String, String>

    @Mock
    lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var redisAdapter: RedisAdapter

    companion object {
        private const val KEY = "test-key"
        private const val VALUE = "test-value"
    }

    @BeforeEach
    fun setup() {
        // Usamos lenient directamente desde mockito-kotlin si es posible
        // o el estático de Mockito para permitir que delete() no use este stub
        lenient().whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)
        redisAdapter = RedisAdapter(redisTemplate)
    }

    @Test
    fun shouldSaveToRedis() {
        // GIVEN
        val duration = Duration.ofMinutes(10)

        // WHEN
        redisAdapter.save(KEY, VALUE, duration)

        // THEN
        verify(valueOperations).set(KEY, VALUE, duration)
    }

    @Test
    fun shouldGetFromRedis() {
        // GIVEN
        whenever(valueOperations.get(KEY)).thenReturn(VALUE)

        // WHEN
        val result = redisAdapter.get(KEY)

        // THEN
        assertEquals(VALUE, result)
        verify(valueOperations).get(KEY)
    }

    @Test
    fun shouldDeleteFromRedis() {
        // WHEN
        redisAdapter.delete(KEY)

        // THEN
        verify(redisTemplate).delete(KEY)
    }
}