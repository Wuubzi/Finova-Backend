package com.wuubzi.user.application.Services

import com.wuubzi.user.application.DTOS.Request.UpdateRequestDTO
import com.wuubzi.user.application.Ports.In.UpdateUserUseCase
import com.wuubzi.user.application.Ports.Out.UserRepositoryPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UpdateUserService(
    private val userRepository: UserRepositoryPort
): UpdateUserUseCase {
    override fun updateUser(userId: UUID, userRequest: UpdateRequestDTO) {
        val user = userRepository.findByIdUser(userId) ?: throw IllegalArgumentException("User with id $userId not found")

        val updatedUser = user.copy(
            firstName = userRequest.firstName,
            lastName = userRequest.lastName,
            documentNumber = userRequest.documentNumber,
            phone = userRequest.phone,
            address = userRequest.address
        )

        userRepository.save(updatedUser)
    }
}