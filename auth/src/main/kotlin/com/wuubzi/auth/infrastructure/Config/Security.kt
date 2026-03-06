package com.wuubzi.auth.infrastructure.Config

import com.wuubzi.auth.infrastructure.Filters.JwtAuthentication
import com.wuubzi.auth.infrastructure.Security.CustomAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class Security(
    private val jwtAuthentication: JwtAuthentication,
    private val authenticationEntryPoint: CustomAuthenticationEntryPoint
) {


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            .csrf { it.disable() }
            .cors { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling { it.authenticationEntryPoint(authenticationEntryPoint) }
            .addFilterBefore(jwtAuthentication, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}