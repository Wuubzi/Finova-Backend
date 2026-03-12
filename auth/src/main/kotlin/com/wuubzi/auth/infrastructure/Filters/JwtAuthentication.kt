package com.wuubzi.auth.infrastructure.Filters

import com.wuubzi.auth.Utils.Jwt
import com.wuubzi.auth.infrastructure.Security.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthentication(
    private val jwtUtils: Jwt,
    private val userDetailsService: CustomUserDetailsService
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = parseJwt(request)
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                val username: String = jwtUtils.getUsername(jwt)
                val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
               authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (ex: Exception) {
            logger.error("Cannot set user authentication: {}", ex)
        }
        filterChain.doFilter(request, response)
    }

    private fun parseJwt(request: HttpServletRequest): String? {
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7)
        }
        return null
    }


}