package com.wuubzi.user.Controllers

import com.wuubzi.user.application.DTOS.Request.UpdateRequestDTO
import com.wuubzi.user.application.Ports.In.DeleteUserUseCase
import com.wuubzi.user.application.Ports.In.GetUserUseCase
import com.wuubzi.user.application.Ports.In.UpdateUserUseCase
import com.wuubzi.user.domain.Models.User
import com.wuubzi.user.infrastructure.Controllers.UserController
import com.wuubzi.user.utils.DateFormatter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.sql.Timestamp
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UserControllerTest {

    @Mock
    lateinit var getUserUseCase: GetUserUseCase

    @Mock
    lateinit var updateUserUseCase: UpdateUserUseCase

    @Mock
    lateinit var deleteUserUseCase: DeleteUserUseCase

    @Mock
    lateinit var dateFormatter: DateFormatter

    @Mock
    lateinit var request: HttpServletRequest

    @InjectMocks
    lateinit var userController: UserController

    private val userId = UUID.randomUUID()
    private val mockDate = "2026-03-20"
    private val mockUrl = "http://localhost/api/v1/users/me"

    @Test
    fun shouldGetUserSuccessfully() {
        // GIVEN
        val mockUser = User(
            idUser = userId,
            firstName = "Carlos",
            lastName = "perez",
            phone = "1234567890",
            address = "Calle 123",
            documentNumber = "987654321",
            profileUrl = "http://example.com/profile.jpg",
            createdAt = Timestamp(System.currentTimeMillis())
        )
        whenever(getUserUseCase.getUser(userId)).thenReturn(mockUser)

        // WHEN
        val response = userController.getUser(userId)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockUser, response.body)
        verify(getUserUseCase).getUser(userId)
    }

    @Test
    fun shouldUpdateUserSuccessfully() {
        // GIVEN
        val updateRequest = UpdateRequestDTO(firstName = "Carlos", lastName = "Vives", address = "Calle 123", phone = "1234567891", documentNumber = "987654321")
        whenever(request.requestURL).thenReturn(StringBuffer(mockUrl))
        whenever(dateFormatter.getDate()).thenReturn(mockDate)

        // WHEN
        val response = userController.updateUser(userId, updateRequest, request)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Usuario actualizado exitosamente", response.body?.message)
        assertEquals(mockUrl, response.body?.url)
        assertEquals(HttpServletResponse.SC_OK, response.body?.code)
        assertEquals(mockDate, response.body?.timestamp)

        verify(updateUserUseCase).updateUser(userId, updateRequest)
    }

    @Test
    fun shouldDeleteUserSuccessfully() {
        // GIVEN
        whenever(request.requestURL).thenReturn(StringBuffer(mockUrl))
        whenever(dateFormatter.getDate()).thenReturn(mockDate)

        // WHEN
        val response = userController.deleteUser(userId, request)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Usuario eliminado exitosamente", response.body?.message)
        assertEquals(mockUrl, response.body?.url)
        assertEquals(mockDate, response.body?.timestamp)

        verify(deleteUserUseCase).deleteUser(userId)
    }
}