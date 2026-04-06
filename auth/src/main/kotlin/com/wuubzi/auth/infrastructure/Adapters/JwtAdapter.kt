package com.wuubzi.auth.infrastructure.Adapters

import com.wuubzi.auth.Utils.Jwt
import com.wuubzi.auth.application.Ports.out.JwtPort
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class JwtAdapter(
    private val jwtUtils: Jwt
): JwtPort {
    override fun generateToken(userId: UUID, email: String): String {
       return jwtUtils.generateToken(userId, email)
    }

    override fun validateToken(token: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun generateRefreshToken(): String {
        return jwtUtils.generateRefreshToken()
    }

}