package com.wuubzi.auth.infrastructure.Adapters

import com.wuubzi.auth.Utils.OtpGenerator
import com.wuubzi.auth.application.Ports.out.OtpPort
import org.springframework.stereotype.Component

@Component
class OtpAdapter(
    private val otpGenerator: OtpGenerator
): OtpPort {
    override fun generateOtp(): String {
        return otpGenerator.generateOtp()
    }
}