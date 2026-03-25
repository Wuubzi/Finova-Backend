package com.wuubzi.account.infrastructure.KafkaConsumers

import com.wuubzi.account.application.DTOS.Events.UserDeletedEvent
import com.wuubzi.account.infrastructure.Persistence.Entities.UserCacheEntity
import com.wuubzi.account.infrastructure.Repositories.UserCacheRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserDeletedTest {

    @Mock
    lateinit var userRepository: UserCacheRepository

    @InjectMocks
    lateinit var userDeleted: UserDeleted

    private val userId = UUID.randomUUID()

    @Test
    fun shouldDeleteUserFromCacheWhenExists() {
        // GIVEN: El evento de eliminación
        val event = UserDeletedEvent(idUser = userId)
        val existingUser = UserCacheEntity().apply { this.userId = userId }

        // Simulamos que el usuario SÍ está en la caché para que el elvis operator (?:) no dispare la excepción
        whenever(userRepository.findByUserId(userId)).thenReturn(existingUser)

        // WHEN: El listener recibe el mensaje
        userDeleted.listen(event)

        // THEN: Verificamos que primero busque y luego borre
        verify(userRepository).findByUserId(userId)
        verify(userRepository).deleteById(userId)
    }

    @Test
    fun shouldThrowExceptionWhenUserToDeleteNotFound() {
        // GIVEN
        val event = UserDeletedEvent(idUser = userId)

        // Simulamos Cache Miss (el usuario no existe)
        whenever(userRepository.findByUserId(userId)).thenReturn(null)

        // WHEN & THEN: Verificamos que lance la IllegalArgumentException
        val exception = assertThrows<IllegalArgumentException> {
            userDeleted.listen(event)
        }

        assertEquals("User with id $userId not found", exception.message)

        // CRÍTICO: Verificamos que NUNCA se llame al deleteById si no se encontró el usuario
        verify(userRepository, never()).deleteById(userId)
    }
}