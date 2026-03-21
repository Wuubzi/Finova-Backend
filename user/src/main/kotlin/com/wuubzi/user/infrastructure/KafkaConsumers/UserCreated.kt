package com.wuubzi.user.infrastructure.KafkaConsumers

import com.wuubzi.user.application.DTOS.Events.UserRequestEvent
import com.wuubzi.user.infrastructure.Persistence.Entities.UserEntity
import com.wuubzi.user.infrastructure.Repositories.UserRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.sql.Timestamp


@Component
class UserCreated(
    private val userRepository: UserRepository
){

    @KafkaListener(
        topics = ["user-created"],
        groupId = "user-microservice"
    )
    fun listen(data: UserRequestEvent) {
        val userExists = userRepository.findByDocumentNumber(data.documentNumber)
        require(userExists == null) {
            "User with document number ${data.documentNumber} already exists"
        }
        val userCreated = UserEntity().apply {
            idUser = data.idUser
            firstName = data.firstName
            lastName = data.lastName
            documentNumber = data.documentNumber
            address = data.address
            phone = data.phoneNumber
            profileUrl = data.profileUrl
            createdAt = Timestamp(System.currentTimeMillis())
        }
        userRepository.save(userCreated)
    }

}