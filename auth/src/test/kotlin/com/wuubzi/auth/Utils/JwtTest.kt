package com.wuubzi.auth.Utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import java.util.UUID

class JwtTest {
    private lateinit var jwt: Jwt

    private val secret = "dGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQ="

    @BeforeEach
    fun setup() {
        jwt = Jwt()
        jwt.secret = secret
        jwt.init()
    }

    @Test
    fun shouldGenerateValidToken() {
        val userId = UUID.randomUUID()

        val token = jwt.generateToken(userId)

        assertNotNull(token)
        assertTrue(jwt.validateToken(token))
    }

    @Test
    fun shouldExtractUsernameFromToken() {
        val userId = UUID.randomUUID()

        val token = jwt.generateToken(userId)

        val subject = jwt.getUsername(token)

        assertEquals(userId.toString(), subject)
    }

    @Test
    fun shouldReturnFalseForInvalidToken() {
        val result = jwt.validateToken("invalid-token")

        assertFalse(result)
    }

    @Test
    fun shouldGenerateRefreshToken() {
        val refreshToken = jwt.generateRefreshToken()

        assertNotNull(refreshToken)
        assertTrue(refreshToken.length > 20)
    }

}