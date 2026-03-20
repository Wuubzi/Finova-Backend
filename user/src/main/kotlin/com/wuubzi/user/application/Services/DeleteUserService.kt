package com.wuubzi.user.application.Services

import com.wuubzi.user.application.DTOS.Events.UserDeletedEvent
import com.wuubzi.user.application.Ports.In.DeleteUserUseCase
import com.wuubzi.user.application.Ports.Out.KafkaPort
import com.wuubzi.user.application.Ports.Out.UserRepositoryPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeleteUserService(
    private val userRepository: UserRepositoryPort,
    private val kafka: KafkaPort
): DeleteUserUseCase {
    override fun deleteUser(userId: UUID) {
        userRepository.findByIdUser(userId) ?: throw IllegalArgumentException("User with id $userId not found")
        val user = UserDeletedEvent(
            idUser = userId
        )
        userRepository.delete(userId)
        kafka.publishUserDeleted(user)
    }
}