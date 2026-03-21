package com.wuubzi.user.KafkaConsumers

import com.wuubzi.user.application.DTOS.Events.UserRequestEvent
import com.wuubzi.user.infrastructure.KafkaConsumers.UserCreated
import com.wuubzi.user.infrastructure.Persistence.Entities.UserEntity
import com.wuubzi.user.infrastructure.Repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UserCreatedTest {

    @Mock
    lateinit var userRepository: UserRepository

    @InjectMocks
    lateinit var userCreatedConsumer: UserCreated

    @Test
    fun shouldCreateUserWhenNotExists() {
        // GIVEN
        val event = UserRequestEvent(
            idUser = UUID.randomUUID(),
            firstName = "Junior",
            lastName = "Barranquilla",
            documentNumber = "12345678",
            address = "Calle 72",
            phoneNumber = "3001234567",
            profileUrl = "https://wuubzi.com/photo.jpg"
        )

        whenever(userRepository.findByDocumentNumber(event.documentNumber)).thenReturn(null)

        // WHEN
        userCreatedConsumer.listen(event)

        // THEN
        val userCaptor = argumentCaptor<UserEntity>()
        verify(userRepository).save(userCaptor.capture())

        val savedUser = userCaptor.firstValue
        assertEquals(event.idUser, savedUser.idUser)
        assertEquals(event.documentNumber, savedUser.documentNumber)
        assertEquals(event.firstName, savedUser.firstName)
        verify(userRepository).findByDocumentNumber(event.documentNumber)
    }

    @Test
    fun shouldThrowExceptionWhenUserAlreadyExists() {
        // GIVEN
        val event = UserRequestEvent(
            idUser = UUID.randomUUID(),
            documentNumber = "87654321",
            firstName = "Existing",
            lastName = "User",
            address = "Existing Address",
            phoneNumber = "987654321",
            profileUrl = "https://existing.com/photo.jpg"
        )
        val existingUser = UserEntity()

        whenever(userRepository.findByDocumentNumber(event.documentNumber)).thenReturn(existingUser)

        // WHEN & THEN
        val exception = assertThrows(IllegalArgumentException::class.java) {
            userCreatedConsumer.listen(event)
        }

        assertTrue(exception.message!!.contains("already exists"))
        verify(userRepository, never()).save(any())
    }
}