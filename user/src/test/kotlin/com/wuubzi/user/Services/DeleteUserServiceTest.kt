package com.wuubzi.user.Services

import com.wuubzi.user.application.DTOS.Events.UserDeletedEvent
import com.wuubzi.user.application.Ports.Out.KafkaPort
import com.wuubzi.user.application.Ports.Out.UserRepositoryPort
import com.wuubzi.user.application.Services.DeleteUserService
import com.wuubzi.user.domain.Models.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
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
import java.sql.Timestamp
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class DeleteUserServiceTest {

    @Mock
    lateinit var userRepository: UserRepositoryPort

    @Mock
    lateinit var kafka: KafkaPort

    @InjectMocks
    lateinit var deleteUserService: DeleteUserService

    private val userId = UUID.randomUUID()

    @Test
    fun shouldDeleteUserSuccessfully() {
        // GIVEN: El usuario existe en el repositorio
        val mockUser = User(
            idUser = userId,
            firstName = "Junior",
            lastName = "Tu Papa",
            documentNumber = "12345",
            phone = "300",
            profileUrl = "url",
            address = "calle 72",
            createdAt = Timestamp(System.currentTimeMillis())
        )
        whenever(userRepository.findByIdUser(userId)).thenReturn(mockUser)

        // WHEN
        deleteUserService.deleteUser(userId)

        // THEN
        verify(userRepository).findByIdUser(userId)
        verify(userRepository).delete(userId)

        // Verificamos que se publique el evento correcto en Kafka
        val eventCaptor = argumentCaptor<UserDeletedEvent>()
        verify(kafka).publishUserDeleted(eventCaptor.capture())
        assertEquals(userId, eventCaptor.firstValue.idUser)
    }

    @Test
    fun shouldThrowExceptionWhenUserNotFound() {
        // GIVEN: El repositorio devuelve null
        whenever(userRepository.findByIdUser(userId)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows(IllegalArgumentException::class.java) {
            deleteUserService.deleteUser(userId)
        }

        assertEquals("User with id $userId not found", exception.message)

        // Verificamos que NO se llame al delete ni al kafka si falla la validación
        verify(userRepository, never()).delete(any())
        verify(kafka, never()).publishUserDeleted(any())
    }
}