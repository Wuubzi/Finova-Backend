package com.wuubzi.auth.TestUnits.Adapters

import com.wuubzi.auth.infrastructure.Adapters.RedisAdapter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

@ExtendWith(MockitoExtension::class)
const val KEY = "test-key"
class RedisAdapterTest {

    @Mock
    lateinit var redisTemplate: RedisTemplate<String, String>

    @Mock
    lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var redisAdapter: RedisAdapter

    @BeforeEach
    fun setup() {
        lenient().whenever(redisTemplate.opsForValue()).doReturn(valueOperations)
        redisAdapter = RedisAdapter(redisTemplate)
    }

    @Test
    fun shouldSaveToRedis() {
        // GIVEN
        val value = "test-value"
        val duration = Duration.ofMinutes(10)

        // WHEN
        redisAdapter.save(KEY, value, duration)

        // THEN
        verify(valueOperations).set(KEY, value, duration)
    }

    @Test
    fun shouldGetFromRedis() {
        // GIVEN
        val expectedValue = "test-value"
        whenever(valueOperations.get(KEY)).thenReturn(expectedValue)

        // WHEN
        val result = redisAdapter.get(KEY)

        // THEN
        assert(result == expectedValue)
        verify(valueOperations).get(KEY)
    }

    @Test
    fun shouldDeleteFromRedis() {
        // GIVEN

        // WHEN
        redisAdapter.delete(KEY)

        // THEN
        verify(redisTemplate).delete(KEY)
    }
}