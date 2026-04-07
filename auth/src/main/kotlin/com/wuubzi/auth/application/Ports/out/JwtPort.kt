package com.wuubzi.auth.application.Ports.out

import java.util.UUID

interface JwtPort {
    fun generateToken(userId: UUID, email: String): String
    fun validateToken(token: String): Boolean
    fun generateRefreshToken(): String
}