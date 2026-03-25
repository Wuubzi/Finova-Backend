package com.wuubzi.account.infrastructure.KafkaConsumers

import com.wuubzi.account.application.DTOS.Events.UserDeletedEvent
import com.wuubzi.account.infrastructure.Repositories.UserCacheRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class UserDeleted(
    private val userRepository: UserCacheRepository
) {
    @KafkaListener(
        topics = ["user-deleted"],
        groupId = "account-microservice",
        containerFactory = "userDeletedKafkaListenerContainerFactory"
    )
    fun listen(data: UserDeletedEvent) {
        userRepository.findByUserId(data.idUser) ?: throw IllegalArgumentException("User with id ${data.idUser} not found")
        userRepository.deleteById(data.idUser)
    }
}