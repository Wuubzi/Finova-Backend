package com.wuubzi.auth.infrastructure.Security

import com.wuubzi.auth.application.Ports.out.PasswordEncoderPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoderAdapter(
    private val passwordEncoder: PasswordEncoder
): PasswordEncoderPort {
    override fun encode(password: String): String {
        return passwordEncoder.encode(password) ?: ""
    }

    override fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }

}