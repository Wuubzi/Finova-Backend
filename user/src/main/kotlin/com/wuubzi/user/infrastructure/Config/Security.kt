package com.wuubzi.user.infrastructure.Config

import com.wuubzi.user.infrastructure.Security.CustomAuthenticationEntryPoint
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import javax.crypto.SecretKey


@Configuration
@EnableWebSecurity
class Security(
    @Value($$"${jwt.secret}")
    private var secret: String,
    private val authenticationEntryPoint: CustomAuthenticationEntryPoint
) {

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val keyBytes: ByteArray? = Decoders.BASE64.decode(secret)
        val key: SecretKey = Keys.hmacShaKeyFor(keyBytes)

        return NimbusJwtDecoder.withSecretKey(key).build()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {it.anyRequest().authenticated() }
            .exceptionHandling {
                it.authenticationEntryPoint(authenticationEntryPoint)
            }
            .oauth2ResourceServer {
                it.jwt {  }
                it.authenticationEntryPoint(authenticationEntryPoint)
            }
        return http.build()
    }

}