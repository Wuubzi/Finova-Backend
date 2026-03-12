package com.wuubzi.auth.TestUnits.Adapters

import com.wuubzi.auth.Utils.OtpGenerator
import com.wuubzi.auth.infrastructure.Adapters.OtpAdapter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class OtpAdapterTest {

    @Mock
    lateinit var otpGenerator: OtpGenerator

    @InjectMocks
    lateinit var otpAdapter: OtpAdapter

    @Test
    fun shouldGenerateOtp() {
        // GIVEN
        val expectedOtp = "654321"
        whenever(otpGenerator.generateOtp()).thenReturn(expectedOtp)

        // WHEN
        val result = otpAdapter.generateOtp()

        // THEN
        assertEquals(expectedOtp, result)
        verify(otpGenerator).generateOtp()
    }
}