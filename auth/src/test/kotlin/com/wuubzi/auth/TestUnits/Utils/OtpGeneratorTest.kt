package com.wuubzi.auth.TestUnits.Utils

import com.wuubzi.auth.Utils.OtpGenerator
import org.junit.jupiter.api.Test

class OtpGeneratorTest {
    private val otpGenerator = OtpGenerator()

 @Test
    fun `generateOtp should return a 6-digit numeric string`() {
        val otp = otpGenerator.generateOtp()
        assert(otp.length == 6) { "OTP should be 6 digits long" }
        assert(otp.all { it.isDigit() }) { "OTP should contain only digits" }
    }
}