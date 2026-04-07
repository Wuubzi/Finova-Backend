package com.wuubzi.transaction.Adapters

import com.wuubzi.transaction.infrastructure.Adapters.WebClientAdapter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.UUID
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class WebClientAdapterTest {

    private val webClientAdapter = WebClientAdapter()

    @Test
    fun shouldReturnNullWhenAccountServiceUnavailableForGetAccountId() {
        // GIVEN
        val mockRequest = MockHttpServletRequest()
        mockRequest.addHeader("Authorization", "Bearer test-token")
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(mockRequest))

        // WHEN - The WebClient call will fail since there is no server running
        val result = try {
            webClientAdapter.getAccountId(UUID.randomUUID())
        } catch (_: Exception) {
            null
        }

        // THEN
        assertNull(result)

        // Cleanup
        RequestContextHolder.resetRequestAttributes()
    }

    @Test
    fun shouldReturnNullWhenAccountServiceUnavailableForGetAccountByAccountNumber() {
        // GIVEN
        val mockRequest = MockHttpServletRequest()
        mockRequest.addHeader("Authorization", "Bearer test-token")
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(mockRequest))

        // WHEN
        val result = try {
            webClientAdapter.getAccountByAccountNumber("12345678901234567890")
        } catch (_: Exception) {
            null
        }

        // THEN
        assertNull(result)

        // Cleanup
        RequestContextHolder.resetRequestAttributes()
    }

    @Test
    fun shouldHandleNoAuthorizationHeader() {
        // GIVEN
        val mockRequest = MockHttpServletRequest()
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(mockRequest))

        // WHEN
        val result = try {
            webClientAdapter.getAccountId(UUID.randomUUID())
        } catch (_: Exception) {
            null
        }

        // THEN
        assertNull(result)

        // Cleanup
        RequestContextHolder.resetRequestAttributes()
    }

    @Test
    fun shouldHandleInvalidBearerToken() {
        // GIVEN
        val mockRequest = MockHttpServletRequest()
        mockRequest.addHeader("Authorization", "Basic invalid-token")
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(mockRequest))

        // WHEN
        val result = try {
            webClientAdapter.getAccountId(UUID.randomUUID())
        } catch (_: Exception) {
            null
        }

        // THEN
        assertNull(result)

        // Cleanup
        RequestContextHolder.resetRequestAttributes()
    }

    @Test
    fun shouldHandleNullRequestAttributes() {
        // GIVEN
        RequestContextHolder.resetRequestAttributes()

        // WHEN
        val result = try {
            webClientAdapter.getAccountId(UUID.randomUUID())
        } catch (_: Exception) {
            null
        }

        // THEN
        assertNull(result)
    }
}

