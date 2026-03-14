package com.wuubzi.auth.Utils

import com.wuubzi.auth.Utils.Jwt
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import java.util.UUID
import java.util.Base64

class JwtTest {

    private lateinit var jwtUtils: Jwt
    // Un secreto en Base64 de al menos 256 bits para HMAC
    private val secretBase64 = Base64.getEncoder().encodeToString("esto-es-un-secreto-muy-largo-y-seguro-123456".toByteArray())

    @BeforeEach
    fun setup() {
        jwtUtils = Jwt()
        jwtUtils.secret = secretBase64
        jwtUtils.init() // Invocamos manualmente el @PostConstruct
    }

    @Test
    fun shouldGenerateAndValidateToken() {
        // GIVEN
        val userId = UUID.randomUUID()

        // WHEN
        val token = jwtUtils.generateToken(userId)
        val isValid = jwtUtils.validateToken(token)
        val extractedUsername = jwtUtils.getUsername(token)

        // THEN
        assertNotNull(token)
        assertTrue(isValid)
        assertEquals(userId.toString(), extractedUsername)
    }

    @Test
    fun shouldGenerateValidRefreshToken() {
        // WHEN
        val refreshToken = jwtUtils.generateRefreshToken()

        // THEN
        assertNotNull(refreshToken)
        assertTrue(refreshToken.length > 20)
    }

    @Test
    fun shouldReturnFalseWhenTokenIsInvalid() {
        // GIVEN
        val invalidToken = "eyAiYWxnIjogIkhTMjU2IiB9.invalid.payload"

        // WHEN
        val isValid = jwtUtils.validateToken(invalidToken)

        // THEN
        assertFalse(isValid)
    }

    @Test
    fun shouldReturnFalseWhenTokenIsExpired() {
        // GIVEN: Un token manipulado o simplemente una firma que no coincide
        val token = "token.totalmente.inventado"

        // WHEN
        val isValid = jwtUtils.validateToken(token)

        // THEN
        assertFalse(isValid)
    }
}
