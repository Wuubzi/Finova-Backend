package com.wuubzi.user.application.Services

import com.wuubzi.user.application.Ports.In.GetUserUseCase
import com.wuubzi.user.application.Ports.Out.CachePort
import com.wuubzi.user.application.Ports.Out.UserRepositoryPort
import com.wuubzi.user.domain.Models.User
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import java.time.Duration
import java.util.UUID

@Service
class GetUserService(
    private val userRepository: UserRepositoryPort,
    private val cachePort: CachePort,
): GetUserUseCase {

    companion object {
        private const val CACHE_PREFIX = "user:"
        private val CACHE_TTL = Duration.ofMinutes(15)
    }

    override fun getUser(userId: UUID): User {
        val cacheKey = "$CACHE_PREFIX$userId"
        val cachedUser = cachePort.getObject(cacheKey, User::class.java)
        if (cachedUser != null) {
            return cachedUser
        }
       val user = userRepository.findByIdUser(userId) ?: throw IllegalArgumentException("User with id $userId not found")
        cachePort.saveObject(cacheKey, user, CACHE_TTL)
        return user
    }
}