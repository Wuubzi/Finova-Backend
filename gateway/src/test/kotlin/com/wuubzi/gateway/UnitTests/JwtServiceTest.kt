package com.wuubzi.gateway.UnitTests

import com.wuubzi.gateway.Security.JwtService
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.ActiveProfiles
import java.nio.charset.StandardCharsets
import java.util.Date
import kotlin.test.assertEquals

@ActiveProfiles("test")
class JwtServiceTest {

    @Value("\${jwt.secret}")
    lateinit var secret: String


    private val jwtService = JwtService(secret)
    private val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    private fun generateValidToken(): String {
        return Jwts.builder()
            .subject("carlos")
            .expiration(Date(System.currentTimeMillis() + 60000))
            .signWith(key)
            .compact()
    }

    @Test
    fun shouldValidateValidToken() {
        val token = generateValidToken()
        val subject = jwtService.validateToken(token)
        assertEquals("carlos", subject)
    }

    @Test
    fun shouldThrowExceptionForInvalidToken() {
        assertThrows(JwtException::class.java) {
            jwtService.validateToken("invalid-token")
        }
    }
}