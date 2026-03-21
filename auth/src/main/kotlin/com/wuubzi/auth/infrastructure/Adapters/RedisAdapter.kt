package com.wuubzi.auth.infrastructure.Adapters

import com.wuubzi.auth.application.Ports.out.CachePort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisAdapter(
    private val redisTemplate: RedisTemplate<String, String>
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
}