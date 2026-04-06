package com.wuubzi.transaction.infrastructure.Adapters

import com.fasterxml.jackson.databind.ObjectMapper
import com.wuubzi.transaction.application.Ports.Out.CachePort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CacheAdapter(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
): CachePort {
    override fun save(key: String, value: String, duration: Duration) {
        redisTemplate.opsForValue().set(key, value, duration)
    }

    override fun get(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    override fun delete(key: String) {
        redisTemplate.delete(key)
    }

    override fun <T> saveObject(key: String, value: T, duration: Duration) {
        val json = objectMapper.writeValueAsString(value)
        redisTemplate.opsForValue().set(key, json, duration)
    }

    override fun <T> getObject(key: String, clazz: Class<T>): T? {
        val json = redisTemplate.opsForValue().get(key) ?: return null
        return try {
            objectMapper.readValue(json, clazz)
        } catch (e: Exception) {
            null
        }
    }
}

