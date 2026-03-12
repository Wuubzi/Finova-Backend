package com.wuubzi.auth.TestUnits.Filters

import com.wuubzi.auth.Utils.Jwt
import com.wuubzi.auth.infrastructure.Filters.JwtAuthentication
import com.wuubzi.auth.infrastructure.Security.CustomUserDetailsService
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

@ExtendWith(MockitoExtension::class)
class JwtAuthenticationTest {

    @Mock
    lateinit var jwtUtils: Jwt

    @Mock
    lateinit var userDetailsService: CustomUserDetailsService

    @Mock
    lateinit var filterChain: FilterChain

    @InjectMocks
    lateinit var jwtAuthentication: JwtAuthentication

    @BeforeEach
    fun setup() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun shouldAuthenticateWhenTokenIsValid() {
        // GIVEN
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer valid-token")
        val response = MockHttpServletResponse()

        val username = "test@wuubzi.com"
        val userDetails = mock<UserDetails>()
        whenever(userDetails.authorities).thenReturn(mutableListOf())

        whenever(jwtUtils.validateToken("valid-token")).thenReturn(true)
        whenever(jwtUtils.getUsername("valid-token")).thenReturn(username)
        whenever(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails)

        // WHEN
        jwtAuthentication.doFilter(request, response, filterChain)

        // THEN
        assert(SecurityContextHolder.getContext().authentication != null)
        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun shouldNotAuthenticateWhenTokenIsMissing() {
        // GIVEN
        val request = MockHttpServletRequest() // Sin header de Auth
        val response = MockHttpServletResponse()

        // WHEN
        jwtAuthentication.doFilter(request, response, filterChain)

        // THEN
        assert(SecurityContextHolder.getContext().authentication == null)
        verify(filterChain).doFilter(request, response)
        verify(jwtUtils, never()).validateToken(any())
    }

    @Test
    fun shouldNotAuthenticateWhenTokenIsInvalid() {
        // GIVEN
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer invalid-token")
        val response = MockHttpServletResponse()

        whenever(jwtUtils.validateToken("invalid-token")).thenReturn(false)

        // WHEN
        jwtAuthentication.doFilter(request, response, filterChain)

        // THEN
        assert(SecurityContextHolder.getContext().authentication == null)
        verify(filterChain).doFilter(request, response)
    }
}