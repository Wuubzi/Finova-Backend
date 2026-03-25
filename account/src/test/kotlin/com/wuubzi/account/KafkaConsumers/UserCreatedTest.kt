
package com.wuubzi.account.infrastructure.KafkaConsumers

import com.wuubzi.account.application.DTOS.Events.UserRequestEvent
import com.wuubzi.account.infrastructure.Persistence.Entities.UserCacheEntity
import com.wuubzi.account.infrastructure.Repositories.UserCacheRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserCreatedTest {

    @Mock
    lateinit var userRepository: UserCacheRepository

    @InjectMocks
    lateinit var userCreated: UserCreated

    private val userId = UUID.randomUUID()

    @Test
    fun shouldSaveUserInCacheWhenNotExists() {
        // GIVEN: El evento que llega por Kafka
        val event = UserRequestEvent(
            idUser = userId,
            firstName = "test",
            lastName = "test",
            documentNumber = "12312312312",
            phoneNumber = "3232323",
            profileUrl = "example.com/photo.jpg",
            address = "test address"
        )

        // Simulamos que el usuario NO existe en la caché (findByUserId devuelve null)
        whenever(userRepository.findByUserId(userId)).thenReturn(null)

        // WHEN: Se ejecuta el listener
        userCreated.listen(event)

        // THEN: Verificamos que se llame al save con una entidad que tenga el ID correcto
        verify(userRepository).save(any<UserCacheEntity>())
        verify(userRepository).findByUserId(userId)
    }

    @Test
    fun shouldThrowExceptionWhenUserAlreadyExistsInCache() {
        // GIVEN
        val event = UserRequestEvent(
            idUser = userId,
            firstName = "testq",
            lastName = "tests",
            documentNumber = "123123212312",
            phoneNumber = "32324323",
            profileUrl = "example.ccom/photo.jpg",
            address = "test addsdress"
        )
        val existingUser = UserCacheEntity().apply { this.userId = userId }

        // Simulamos que el usuario SÍ existe
        whenever(userRepository.findByUserId(userId)).thenReturn(existingUser)

        // WHEN & THEN: Verificamos que el require lance la excepción con el mensaje esperado
        val exception = assertThrows<IllegalArgumentException> {
            userCreated.listen(event)
        }

        assertEquals("User with id $userId already exists", exception.message)
        // Verificamos que NO se llamó al save por culpa del error
        verify(userRepository, never()).save(any())
    }
}