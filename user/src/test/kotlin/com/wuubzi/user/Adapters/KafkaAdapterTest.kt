package com.wuubzi.user.Adapters

import com.wuubzi.user.application.DTOS.Events.UserDeletedEvent
import com.wuubzi.user.infrastructure.Adapters.KafkaAdapter
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
    fun shouldPublishUserDeletedEvent() {
        // GIVEN
        val event = UserDeletedEvent(
            idUser = UUID.randomUUID(),
        )

        // WHEN
        kafkaAdapter.publishUserDeleted(event)

        // THEN
        // Verificamos que se envíe al tópico "user-deleted" con el objeto event
        verify(kafkaTemplate).send("user-deleted", event)
    }
}