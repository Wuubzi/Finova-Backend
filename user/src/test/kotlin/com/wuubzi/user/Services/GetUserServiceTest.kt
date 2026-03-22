package com.wuubzi.user.Services

import com.wuubzi.user.application.Ports.Out.CachePort
import com.wuubzi.user.application.Ports.Out.UserRepositoryPort
import com.wuubzi.user.application.Services.GetUserService
import com.wuubzi.user.domain.Models.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Timestamp
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class GetUserServiceTest {

    @Mock
    lateinit var userRepository: UserRepositoryPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var getUserService: GetUserService

    private val userId = UUID.randomUUID()

    @Test
    fun shouldReturnUserWhenExists() {
        // GIVEN
        val mockUser = User(
            idUser = userId,
            firstName = "Junior",
            lastName = "Tu Papa",
            documentNumber = "12345",
            phone = "300",
            profileUrl = "http://image.png",
            address = "Calle 72",
            createdAt = Timestamp(System.currentTimeMillis())
        )
        whenever(userRepository.findByIdUser(userId)).thenReturn(mockUser)

        // WHEN
        val result = getUserService.getUser(userId)

        // THEN
        assertEquals(userId, result.idUser)
        assertEquals("Junior", result.firstName)
        verify(userRepository).findByIdUser(userId)
    }

    @Test
    fun shouldThrowExceptionWhenUserNotFound() {
        // GIVEN
        whenever(userRepository.findByIdUser(userId)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows(IllegalArgumentException::class.java) {
            getUserService.getUser(userId)
        }

        assertEquals("User with id $userId not found", exception.message)
        verify(userRepository).findByIdUser(userId)
    }
}