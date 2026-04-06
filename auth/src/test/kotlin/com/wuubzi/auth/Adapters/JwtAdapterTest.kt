package com.wuubzi.auth.Adapters

import com.wuubzi.auth.Utils.Jwt
import com.wuubzi.auth.infrastructure.Adapters.JwtAdapter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class JwtAdapterTest {

    @Mock
    lateinit var jwtUtils: Jwt

    @InjectMocks
    lateinit var jwtAdapter: JwtAdapter

    @Test
    fun shouldGenerateToken() {
        // GIVEN
        val userId = UUID.randomUUID()
        val email = "test@finova.com"
        val expectedToken = "mocked-access-token"
        whenever(jwtUtils.generateToken(userId, email)).thenReturn(expectedToken)

        // WHEN
        val result = jwtAdapter.generateToken(userId, email)

        // THEN
        assertEquals(expectedToken, result)
        verify(jwtUtils).generateToken(userId, email)
    }

    @Test
    fun shouldGenerateRefreshToken() {
        // GIVEN
        val expectedRefreshToken = "mocked-refresh-token"
        whenever(jwtUtils.generateRefreshToken()).thenReturn(expectedRefreshToken)

        // WHEN
        val result = jwtAdapter.generateRefreshToken()

        // THEN
        assertEquals(expectedRefreshToken, result)
        verify(jwtUtils).generateRefreshToken()
    }
}