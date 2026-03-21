package com.wuubzi.gateway.Security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value($$"${jwt.secret}")
    private val secret: String
){
    val keyBytes: ByteArray? = Decoders.BASE64.decode(secret)
    val key: SecretKey? = Keys.hmacShaKeyFor(keyBytes)



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