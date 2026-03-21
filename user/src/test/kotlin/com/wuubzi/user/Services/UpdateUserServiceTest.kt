package com.wuubzi.user.Services

import com.wuubzi.user.application.DTOS.Request.UpdateRequestDTO
import com.wuubzi.user.application.Ports.Out.UserRepositoryPort
import com.wuubzi.user.application.Services.UpdateUserService
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
class UpdateUserServiceTest {

    @Mock
    lateinit var userRepository: UserRepositoryPort

    @InjectMocks
    lateinit var updateUserService: UpdateUserService

    private val userId = UUID.randomUUID()

    @Test
    fun `should update user successfully when user exists`() {
        // GIVEN: El usuario original en la base de datos
        val originalUser = User(
            idUser = userId,
            firstName = "Junior",
            lastName = "Viejo",
            documentNumber = "123",
            phone = "300",
            profileUrl = "http://old-image.png",
            address = "Calle Antigua",
            createdAt = Timestamp(System.currentTimeMillis())
        )

        val updateRequest = UpdateRequestDTO(
            firstName = "Junior",
            lastName = "Tu Papa",
            documentNumber = "54321",
            phone = "310",
            address = "Calle 72"
        )

        whenever(userRepository.findByIdUser(userId)).thenReturn(originalUser)

        // WHEN
        updateUserService.updateUser(userId, updateRequest)

        // THEN
        // Capturamos el objeto que se envía al save para validar el copy()
        val userCaptor = argumentCaptor<User>()
        verify(userRepository).save(userCaptor.capture())

        val savedUser = userCaptor.firstValue
        assertEquals(updateRequest.firstName, savedUser.firstName)
        assertEquals(updateRequest.lastName, savedUser.lastName)
        assertEquals(updateRequest.documentNumber, savedUser.documentNumber)
        assertEquals(originalUser.profileUrl, savedUser.profileUrl) // Este no debió cambiar
        assertEquals(originalUser.idUser, savedUser.idUser)

        verify(userRepository).findByIdUser(userId)
    }

    @Test
    fun `should throw exception when user to update is not found`() {
        // GIVEN
        val updateRequest = UpdateRequestDTO(
            firstName = "New",
            lastName = "Name",
            documentNumber = "1",
            phone = "1",
            address = "1"
        )
        whenever(userRepository.findByIdUser(userId)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows(IllegalArgumentException::class.java) {
            updateUserService.updateUser(userId, updateRequest)
        }

        assertEquals("User with id $userId not found", exception.message)
        verify(userRepository, never()).save(any())
    }
}