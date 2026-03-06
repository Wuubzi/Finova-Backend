package com.wuubzi.auth.application.Services

import com.wuubzi.auth.application.Ports.`in`.CreateUserUseCase
import com.wuubzi.auth.application.Ports.out.PasswordEncoderPort
import com.wuubzi.auth.application.Ports.out.UserCredentialsRepositoryPort
import com.wuubzi.auth.domain.models.UserCredentials
import org.springframework.stereotype.Service

@Service
class CreateUserService(
    private val userCredentialsRepository: UserCredentialsRepositoryPort,
    private val passwordEncoder: PasswordEncoderPort
): CreateUserUseCase {
    override fun createUser(userCredentials: UserCredentials): UserCredentials {
        println(userCredentials)
        val userPasswordEncode = userCredentials.copy(
            password = passwordEncoder.encode(userCredentials.password ?: "")
        )

        return userCredentialsRepository.save(userPasswordEncode)

    }

}