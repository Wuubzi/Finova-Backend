package com.wuubzi.auth.TestUnits.Utils

import com.wuubzi.auth.Utils.OtpGenerator
import org.junit.jupiter.api.Test

class OtpGeneratorTest {
    private val otpGenerator = OtpGenerator()

 @Test
    fun generateOtpShouldReturnA6digitNumericString() {
        val otp = otpGenerator.generateOtp()
        assert(otp.length == 6) { "OTP should be 6 digits long" }
        assert(otp.all { it.isDigit() }) { "OTP should contain only digits" }
    }
}