package com.wuubzi.auth.Services

import com.wuubzi.auth.application.DTOS.Request.ValidateOTPRequest
import com.wuubzi.auth.application.Exceptions.InvalidOtpException
import com.wuubzi.auth.application.Ports.out.CachePort
import com.wuubzi.auth.application.Ports.out.UserCredentialsRepositoryPort
import com.wuubzi.auth.application.Services.ValidateOTPService
import com.wuubzi.auth.domain.models.UserCredentials
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Duration
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ValidateOTPServiceTest {

    @Mock
    lateinit var userCredentialsRepository: UserCredentialsRepositoryPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var validateOTPService: ValidateOTPService

    private val email = "test@wuubzi.com"
    private val correctOtp = "123456"

    @Test
    fun shouldValidateOTPSuccessfully() {
        // GIVEN
        val request = ValidateOTPRequest(email, correctOtp)
        val user = mock<UserCredentials>()

        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(user)
        whenever(cachePort.get("recover-password-otp:$email")).thenReturn(correctOtp)

        // WHEN
        val resetToken = validateOTPService.validateOTP(request)

        // THEN
        assertNotNull(resetToken)

        // Verificar que se guardó el nuevo resetToken en cache por 10 min
        verify(cachePort).save(
            eq("reset-password-token:$email"),
            any(),
            eq(Duration.ofMinutes(10))
        )

        // Verificar que se eliminó el OTP usado de la cache
        verify(cachePort).delete("recover-password-otp:$email")
    }

    @Test
    fun shouldThrowInvalidOtpExceptionWhenOtpIsIncorrect() {
        // GIVEN
        val request = ValidateOTPRequest(email, "654321")
        val user = mock<UserCredentials>()

        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(user)
        whenever(cachePort.get("recover-password-otp:$email")).thenReturn(correctOtp)

        // WHEN & THEN
        val exception = assertThrows(InvalidOtpException::class.java) {
            validateOTPService.validateOTP(request)
        }

        assertEquals("The verification code is incorrect or has expired.", exception.message)

        // Verificar que NO se generó el token de reset ni se borró el OTP si falló la validación
        verify(cachePort, never()).save(any(), any(), any())
        verify(cachePort, never()).delete(any())
    }

    @Test
    fun shouldThrowExceptionWhenUserNotFound() {
        // GIVEN
        val request = ValidateOTPRequest(email, correctOtp)
        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows(IllegalArgumentException::class.java) {
            validateOTPService.validateOTP(request)
        }

        assertEquals("User with email $email not found", exception.message)
        verify(cachePort, never()).get(any())
    }
}