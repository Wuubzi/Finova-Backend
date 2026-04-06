package com.wuubzi.account.Services

import com.wuubzi.account.application.Services.GetBalanceAccountService
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.application.Ports.out.CachePort
import com.wuubzi.account.domain.models.AccountModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class GetBalanceAccountServiceTest {

    @Mock
    lateinit var accountRepository: AccountRepositoryPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var getBalanceAccountService: GetBalanceAccountService

    private val userId = UUID.randomUUID()

    @Test
    fun shouldReturnAccountBalanceSuccessfully() {
        // GIVEN: Una cuenta con valores específicos para probar el mapeo
        val mockAccount = AccountModel(
            accountId = UUID.randomUUID(),
            accountNumber = "123456",
            userId = userId,
            accountType = "SAVINGS",
            currency = "USD",
            balance = 5000.0,
            availableBalance = 4500.0,
            status = "ACTIVE",
            alias = "Ahorros",
            overdraftLimit = 500.0,
            createdAt = mock(),
            updatedAt = mock()
        )
        whenever(accountRepository.findByUserId(userId)).thenReturn(mockAccount)

        // WHEN
        val response = getBalanceAccountService.getBalance(userId)

        // THEN: Validamos que el DTO tenga los mismos valores que el modelo
        assertEquals(5000.0, response.balance)
        assertEquals(4500.0, response.availableBalance)
        assertEquals("USD", response.currency)
        assertEquals(500.0, response.overdraftLimit)

        verify(accountRepository).findByUserId(userId)
    }

    @Test
    fun shouldThrowExceptionWhenAccountForBalanceNotFound() {
        // GIVEN
        whenever(accountRepository.findByUserId(userId)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            getBalanceAccountService.getBalance(userId)
        }

        assertEquals("User with id $userId not found", exception.message)
        verify(accountRepository).findByUserId(userId)
    }
}