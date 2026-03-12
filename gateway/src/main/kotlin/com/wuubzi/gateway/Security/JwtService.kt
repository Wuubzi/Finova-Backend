package com.wuubzi.gateway.Security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.Date

@Service
class JwtService(
    @Value($$"${jwt.secret}")
    private val secret: String


){
    private val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun validateToken(token: String): String {
        val jws = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)

        val claims = jws.payload

        if (claims.expiration.before(Date())) {
            throw JwtException("Token expired")
        }

        return claims.subject ?: throw JwtException("Invalid token")
    }
}