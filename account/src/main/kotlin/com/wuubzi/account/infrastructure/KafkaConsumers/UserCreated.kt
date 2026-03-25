package com.wuubzi.account.infrastructure.KafkaConsumers

import com.wuubzi.account.application.DTOS.Events.UserRequestEvent
import com.wuubzi.account.infrastructure.Persistence.Entities.UserCacheEntity
import com.wuubzi.account.infrastructure.Repositories.UserCacheRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class UserCreated(
    private val userRepository: UserCacheRepository
){

    @KafkaListener(
        topics = ["user-created"],
        groupId = "account-microservice",
        containerFactory = "userCreatedKafkaListenerContainerFactory"
    )
    fun listen(data: UserRequestEvent) {
        val user = userRepository.findByUserId(data.idUser)
        require(user == null) { "User with id ${data.idUser} already exists" }

        val userSaved = UserCacheEntity().apply {
            userId = data.idUser
        }

        userRepository.save(userSaved)
    }

}