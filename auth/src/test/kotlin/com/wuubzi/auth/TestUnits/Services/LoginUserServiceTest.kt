package com.wuubzi.auth.TestUnits.Services

import com.wuubzi.auth.application.DTOS.Request.LoginRequest
import com.wuubzi.auth.application.Ports.out.JwtPort
import com.wuubzi.auth.application.Ports.out.PasswordEncoderPort
import com.wuubzi.auth.application.Ports.out.RefreshTokenRepositoryPort
import com.wuubzi.auth.application.Ports.out.UserCredentialsRepositoryPort
import com.wuubzi.auth.application.Services.LoginUserService
import com.wuubzi.auth.domain.models.RefreshToken
import com.wuubzi.auth.domain.models.UserCredentials
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Timestamp
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class LoginUserServiceTest {

    @Mock
    lateinit var userCredentialsRepository: UserCredentialsRepositoryPort

    @Mock
    lateinit var passwordEncoder: PasswordEncoderPort

    @Mock
    lateinit var jwtPort: JwtPort

    @Mock
    lateinit var refreshTokenRepositoryPort: RefreshTokenRepositoryPort

    @InjectMocks
    lateinit var loginUserService: LoginUserService

    private val email = "user@wuubzi.com"
    private val password = "password123"
    private val userId = UUID.randomUUID()

    @Test
    fun shouldLoginSuccessfully() {
        // GIVEN
        val loginRequest = LoginRequest(email, password)
        val user = UserCredentials(
            id = userId,
            userId = UUID.randomUUID(),
            email = email,
            password = "hashed_password",
            role = "USER",
            isActive = true,
            createdAt = Timestamp(System.currentTimeMillis())
        )

        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(user)
        whenever(passwordEncoder.matches(password, user.password)).thenReturn(true)
        whenever(jwtPort.generateRefreshToken()).thenReturn("refresh-token-123")
        whenever(jwtPort.generateToken(userId)).thenReturn("access-token-123")

        // WHEN
        val response = loginUserService.login(loginRequest)

        // THEN
        assertEquals("access-token-123", response.accessToken)
        assertEquals("refresh-token-123", response.refreshToken)

        // Verificar que se guardó el refresh token en la base de datos
        val refreshTokenCaptor = argumentCaptor<RefreshToken>()
        verify(refreshTokenRepositoryPort).save(refreshTokenCaptor.capture())

        val savedToken = refreshTokenCaptor.firstValue
        assertEquals(userId, savedToken.userId)
        assertEquals("refresh-token-123", savedToken.token)
        assertEquals(false, savedToken.isRevoked)
    }

    @Test
    fun shouldThrowExceptionWhenUserNotFound() {
        // GIVEN
        val loginRequest = LoginRequest(email, password)
        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows(IllegalArgumentException::class.java) {
            loginUserService.login(loginRequest)
        }
        assertEquals("User with email $email not found", exception.message)
    }

    @Test
    fun shouldThrowExceptionWhenPasswordIsInvalid() {
        // GIVEN
        val loginRequest = LoginRequest(email, "wrong_password")
        val user = UserCredentials(
            id = userId,
            userId = UUID.randomUUID(),
            email = email,
            password = "hashed_password",
            role = "USER",
            isActive = true,
            createdAt = Timestamp(System.currentTimeMillis())
        )

        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(user)
        whenever(passwordEncoder.matches("wrong_password", user.password)).thenReturn(false)

        // WHEN & THEN
        val exception = assertThrows(IllegalArgumentException::class.java) {
            loginUserService.login(loginRequest)
        }
        assertEquals("Invalid password", exception.message)

        // Verificar que NO se generaron tokens ni se guardó nada
        verify(jwtPort, org.mockito.Mockito.never()).generateToken(any())
        verify(refreshTokenRepositoryPort, org.mockito.Mockito.never()).save(any())
    }
}