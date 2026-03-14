package com.wuubzi.auth.Services

import com.wuubzi.auth.application.DTOS.Events.RecoverPassword
import com.wuubzi.auth.application.Ports.out.CachePort
import com.wuubzi.auth.application.Ports.out.KafkaPort
import com.wuubzi.auth.application.Ports.out.OtpPort
import com.wuubzi.auth.application.Services.RecoverPasswordService
import com.wuubzi.auth.infrastructure.Persistence.Entities.UserCredentialsEntity
import com.wuubzi.auth.infrastructure.Repositories.UserCredentialsRepository
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Duration
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class RecoverPasswordServiceTest {

    @Mock
    lateinit var userCredentialsRepository: UserCredentialsRepository

    @Mock
    lateinit var otpPort: OtpPort

    @Mock
    lateinit var kafkaPort: KafkaPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var recoverPasswordService: RecoverPasswordService

    private val email = "user@wuubzi.com"
    private val generatedOtp = "123456"

    @Test
    fun shouldRecoverPasswordSuccessfully() {
        // GIVEN
        val user = UserCredentialsEntity().apply { this.email = email }
        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(user)
        whenever(otpPort.generateOtp()).thenReturn(generatedOtp)

        // WHEN
        recoverPasswordService.recoverPassword(email)

        // THEN
        // 1. Verificar que se guardó en cache con la key y el tiempo correcto
        verify(cachePort).save(
            eq("recover-password-otp:$email"),
            eq(generatedOtp),
            eq(Duration.ofMinutes(10))
        )

        val requestPasswordEvent = RecoverPassword(email, generatedOtp)

        // 2. Verificar que se publicó el OTP en Kafka
        verify(kafkaPort).publishRecoverPassword(requestPasswordEvent)
    }

    @Test
    fun shouldThrowExceptionWhenUserNotFound() {
        // GIVEN
        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows(IllegalArgumentException::class.java) {
            recoverPasswordService.recoverPassword(email)
        }

        assertEquals("User with email $email not found", exception.message)

        // Verificar que NO se generó OTP ni se guardó nada si el usuario no existe
        verify(otpPort, org.mockito.Mockito.never()).generateOtp()
        verify(cachePort, org.mockito.Mockito.never()).save(any(), any(), any())
        verify(kafkaPort, org.mockito.Mockito.never()).publishRecoverPassword(any())
    }
}