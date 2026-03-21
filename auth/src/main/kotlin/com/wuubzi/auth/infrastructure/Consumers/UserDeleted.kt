package com.wuubzi.auth.infrastructure.Consumers

import com.wuubzi.auth.application.DTOS.Events.UserDeletedEvent
import com.wuubzi.auth.application.Ports.`in`.LogoutUseCase
import com.wuubzi.auth.infrastructure.Repositories.RefreshTokenRepository
import com.wuubzi.auth.infrastructure.Repositories.UserCredentialsRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class UserDeleted(
    private val userCredentialsRepository: UserCredentialsRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val logoutUseCase: LogoutUseCase
) {
    @KafkaListener(
        topics = ["user-deleted"],
        groupId = "auth-microservice"
    )
    fun listen(data:  UserDeletedEvent) {
       val userCredentials = userCredentialsRepository.findByUserId(data.idUser) ?: throw IllegalArgumentException("User with id ${data.idUser} not found")
       val refreskToken = refreshTokenRepository.findByUserId(data.idUser) ?: throw IllegalArgumentException("Refresh token not found")
       userCredentials.isActive = false
       userCredentialsRepository.save(userCredentials)
       logoutUseCase.logout(refreskToken.token)
    }
}