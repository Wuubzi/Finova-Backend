package com.wuubzi.auth.Utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class Jwt {
    @Value($$"${jwt.secret}")
    lateinit var secret: String
    @Value($$"${jwt.expiration}")
    lateinit var expiration: String
    lateinit var key: SecretKey


    @PostConstruct
    fun init() {
        this.key = Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateToken(username: String): String {
        return Jwts.builder()
            .subject(username)
            .signWith(key)
            .compact()
    }

    fun getUsername(token: String): String {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload.subject
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            return true
        } catch (exception: SecurityException) {
            return false
        }
    }
}
