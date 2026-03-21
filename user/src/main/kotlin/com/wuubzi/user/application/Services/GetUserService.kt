package com.wuubzi.user.application.Services

import com.wuubzi.user.application.Ports.In.GetUserUseCase
import com.wuubzi.user.application.Ports.Out.UserRepositoryPort
import com.wuubzi.user.domain.Models.User
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetUserService(
    private val userRepository: UserRepositoryPort
): GetUserUseCase {
    override fun getUser(userId: UUID): User {
       val user = userRepository.findByIdUser(userId) ?: throw IllegalArgumentException("User with id $userId not found")
        return user
    }
}