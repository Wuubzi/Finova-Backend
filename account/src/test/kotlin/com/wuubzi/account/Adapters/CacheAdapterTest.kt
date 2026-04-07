package com.wuubzi.account.Adapters

import com.fasterxml.jackson.databind.ObjectMapper
import com.wuubzi.account.infrastructure.Adapters.CacheAdapter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class CacheAdapterTest {

    @Mock
    lateinit var redisTemplate: RedisTemplate<String, String>

    @Mock
    lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var cacheAdapter: CacheAdapter

    companion object {
        private const val KEY = "test-key"
        private const val VALUE = "test-value"
    }

    @BeforeEach
    fun setup() {
        lenient().whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)
        cacheAdapter = CacheAdapter(redisTemplate, objectMapper)
    }

    @Test
    fun shouldSaveStringValue() {
        // GIVEN
        val duration = Duration.ofMinutes(10)

        // WHEN
        cacheAdapter.save(KEY, VALUE, duration)

        // THEN
        verify(valueOperations).set(KEY, VALUE, duration)
    }

    @Test
    fun shouldGetStringValue() {
        // GIVEN
        whenever(valueOperations.get(KEY)).thenReturn(VALUE)

        // WHEN
        val result = cacheAdapter.get(KEY)

        // THEN
        assertEquals(VALUE, result)
    }

    @Test
    fun shouldReturnNullWhenKeyNotExists() {
        // GIVEN
        whenever(valueOperations.get(KEY)).thenReturn(null)

        // WHEN
        val result = cacheAdapter.get(KEY)

        // THEN
        assertNull(result)
    }

    @Test
    fun shouldDeleteKey() {
        // WHEN
        cacheAdapter.delete(KEY)

        // THEN
        verify(redisTemplate).delete(KEY)
    }

    @Test
    fun shouldSaveObject() {
        // GIVEN
        val duration = Duration.ofMinutes(15)
        val obj = mapOf("name" to "test")
        val json = "{\"name\":\"test\"}"
        whenever(objectMapper.writeValueAsString(obj)).thenReturn(json)

        // WHEN
        cacheAdapter.saveObject(KEY, obj, duration)

        // THEN
        verify(objectMapper).writeValueAsString(obj)
        verify(valueOperations).set(KEY, json, duration)
    }

    @Test
    fun shouldGetObject() {
        // GIVEN
        val json = "{\"name\":\"test\"}"
        val expected = mapOf("name" to "test")
        whenever(valueOperations.get(KEY)).thenReturn(json)
        whenever(objectMapper.readValue(json, Map::class.java)).thenReturn(expected)

        // WHEN
        val result = cacheAdapter.getObject(KEY, Map::class.java)

        // THEN
        assertEquals(expected, result)
    }

    @Test
    fun shouldReturnNullWhenGetObjectKeyNotExists() {
        // GIVEN
        whenever(valueOperations.get(KEY)).thenReturn(null)

        // WHEN
        val result = cacheAdapter.getObject(KEY, Map::class.java)

        // THEN
        assertNull(result)
    }

    @Test
    fun shouldReturnNullWhenDeserializationFails() {
        // GIVEN
        val invalidJson = "invalid-json"
        whenever(valueOperations.get(KEY)).thenReturn(invalidJson)
        whenever(objectMapper.readValue(invalidJson, Map::class.java)).thenThrow(RuntimeException("parse error"))

        // WHEN
        val result = cacheAdapter.getObject(KEY, Map::class.java)

        // THEN
        assertNull(result)
    }
}

