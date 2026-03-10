package com.wuubzi.auth.application.Services

import com.wuubzi.auth.application.DTOS.Events.UserCreated
import com.wuubzi.auth.application.DTOS.Request.UserRequest
import com.wuubzi.auth.application.Exceptions.EmailAlreadyExist
import com.wuubzi.auth.application.Ports.`in`.CreateUserUseCase
import com.wuubzi.auth.application.Ports.out.KafkaPort
import com.wuubzi.auth.application.Ports.out.PasswordEncoderPort
import com.wuubzi.auth.application.Ports.out.UserCredentialsRepositoryPort
import com.wuubzi.auth.domain.models.UserCredentials
import com.wuubzi.auth.infrastructure.Repositories.UserCredentialsRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateUserService(
    private val userCredentialsRepository: UserCredentialsRepositoryPort,
    private val passwordEncoder: PasswordEncoderPort,
    private val kafkaPort: KafkaPort,
): CreateUserUseCase {
    override fun createUser(userRequest: UserRequest): UserCredentials {
        val user = userCredentialsRepository.findByEmail(userRequest.email)
        if (user != null) {
            throw EmailAlreadyExist("User with email ${user.email} already exists")
        }

        val idCredentials = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val userCredentials = UserCredentials(
            id = idCredentials,
            userId = userId,
            email = userRequest.email,
            password = passwordEncoder.encode(userRequest.password ?: ""),
            role = "USER",
            isActive = true,
            createdAt = java.sql.Timestamp(System.currentTimeMillis())
        )

        val createdUser = UserCreated(
            idUser = userId,
            firstName = userRequest.firstName,
            lastName = userRequest.lastName,
            documentNumber = userRequest.documentNumber,
            phoneNumber = userRequest.phoneNumber,
            address = userRequest.address,
        )

        kafkaPort.publishUserCreated(createdUser)
        return userCredentialsRepository.save(userCredentials)
    }

}