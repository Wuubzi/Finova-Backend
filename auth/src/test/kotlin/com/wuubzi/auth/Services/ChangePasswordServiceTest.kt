package com.wuubzi.auth.Services

import com.wuubzi.auth.application.DTOS.Request.ChangePasswordRequest
import com.wuubzi.auth.application.Ports.out.CachePort
import com.wuubzi.auth.application.Services.ChangePasswordService
import com.wuubzi.auth.infrastructure.Persistence.Entities.UserCredentialsEntity
import com.wuubzi.auth.infrastructure.Repositories.UserCredentialsRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import java.sql.Timestamp
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ChangePasswordServiceTest {

    @Mock
    lateinit var userCredentialsRepository: UserCredentialsRepository

    @Mock
    lateinit var cachePort: CachePort

    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    lateinit var changePasswordService: ChangePasswordService

    private val email = "test@wuubzi.com"
    private val validToken = "secure-reset-token"

    @Test
    fun shouldChangePasswordSuccessfully() {
        val request = ChangePasswordRequest(email, validToken, "newPass123", "newPass123")
        val user = UserCredentialsEntity().apply {
            this.id = UUID.randomUUID()
            this.userId = UUID.randomUUID()
            this.email = email
            this.password = "oldPassword"
            this.role = "USER"
            this.isActive = true
            this.createdAt = Timestamp(System.currentTimeMillis())
        }

        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(user)
        whenever(cachePort.get("reset-password-token:$email")).thenReturn(validToken)
        whenever(passwordEncoder.encode(request.password)).thenReturn("encodedNewPassword")

        changePasswordService.changePassword(request)

        val userCaptor = argumentCaptor<UserCredentialsEntity>()
        verify(userCredentialsRepository).save(userCaptor.capture())
        assertEquals("encodedNewPassword", userCaptor.firstValue.password)
    }

    @Test
    fun shouldThrowExceptionWhenUserNotFound() {
        val request = ChangePasswordRequest(email, validToken, "pass", "pass")
        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(null)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            changePasswordService.changePassword(request)
        }
        assertEquals("User with email $email not found", exception.message)
    }

    @Test
    fun shouldThrowExceptionWhenTokenIsInvalid() {
        val request = ChangePasswordRequest(email, "invalid-token", "pass", "pass")
        val user = UserCredentialsEntity().apply {
            this.email = email
        }

        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(user)
        whenever(cachePort.get("reset-password-token:$email")).thenReturn(validToken)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            changePasswordService.changePassword(request)
        }
        assertEquals("Invalid or expired reset token", exception.message)
    }

    @Test
    fun shouldThrowExceptionWhenPasswordsDoNotMatch() {
        val request = ChangePasswordRequest(email, validToken, "pass123", "pass456")
        val user = UserCredentialsEntity().apply {
            this.email = email
        }

        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(user)
        whenever(cachePort.get("reset-password-token:$email")).thenReturn(validToken)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            changePasswordService.changePassword(request)
        }
        assertEquals("Passwords do not match", exception.message)
    }
}