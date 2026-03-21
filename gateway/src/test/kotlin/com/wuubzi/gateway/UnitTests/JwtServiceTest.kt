package com.wuubzi.gateway.UnitTests

import com.wuubzi.gateway.Security.JwtService
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.util.Date
import kotlin.test.assertEquals

private const val TEST_SECRET = "bXlzdXBlcnNlY3JldGtleWZvcnRlc3RpbmdhdGxlYXN0MjU2Yml0c2xvbmdlbm91Z2hmb3JobWFjc2hhMjU2YWxnb3JpdGht"  // ← Removí la 'y' extra al final
@ActiveProfiles("test")
class JwtServiceTest {

    private val jwtService = JwtService(TEST_SECRET)

    // CAMBIO: Decodificar de Base64 igual que hace JwtService
    private val keyBytes = Decoders.BASE64.decode(TEST_SECRET)
    private val key = Keys.hmacShaKeyFor(keyBytes)

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

    @Test
    fun shouldThrowExceptionForExpiredToken() {
        // Token que expira inmediatamente
        val expiredToken = Jwts.builder()
            .subject("carlos")
            .expiration(Date(System.currentTimeMillis() - 1000)) // Ya expiró
            .signWith(key)
            .compact()

        assertThrows(JwtException::class.java) {
            jwtService.validateToken(expiredToken)
        }
    }
}