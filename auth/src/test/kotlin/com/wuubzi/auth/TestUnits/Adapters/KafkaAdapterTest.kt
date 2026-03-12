package com.wuubzi.auth.TestUnits.Adapters

import com.wuubzi.auth.application.DTOS.Events.UserCreated
import com.wuubzi.auth.infrastructure.Adapters.KafkaAdapter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.springframework.kafka.core.KafkaTemplate
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class KafkaAdapterTest {

    @Mock
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @InjectMocks
    lateinit var kafkaAdapter: KafkaAdapter

    @Test
    fun shouldPublishUserCreatedEvent() {
        // GIVEN
        val userCreatedEvent = UserCreated(
            idUser = UUID.randomUUID(),
            firstName = "Test",
            lastName = "User",
            documentNumber =    "123456789",
            phoneNumber = "123456789",
            address = "Test Address",
        )

        // WHEN
        kafkaAdapter.publishUserCreated(userCreatedEvent)

        // THEN
        verify(kafkaTemplate).send("user-created", userCreatedEvent)
    }

    @Test
    fun shouldPublishRecoverPasswordEvent() {
        // GIVEN
        val otp = "123456"

        // WHEN
        kafkaAdapter.publishRecoverPassword(otp)

        // THEN
        verify(kafkaTemplate).send("recover-password", otp)
    }
}