package com.wuubzi.account.Services

import com.wuubzi.account.application.Exceptions.AccountNotFoundException
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.application.Ports.out.CachePort
import com.wuubzi.account.application.Services.GetAccountByAccountNumberService
import com.wuubzi.account.domain.models.AccountModel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class GetAccountByAccountNumberServiceTest {

    @Mock
    lateinit var accountRepository: AccountRepositoryPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var service: GetAccountByAccountNumberService

    private val accountNumber = "12345678901234567890"
    private val now = Timestamp.from(Instant.now())

    private fun createAccountModel() = AccountModel(
        accountId = UUID.randomUUID(),
        accountNumber = accountNumber,
        userId = UUID.randomUUID(),
        accountType = "SAVINGS",
        currency = "USD",
        balance = 1000.0,
        availableBalance = 1000.0,
        status = "ACTIVE",
        alias = "Test",
        overdraftLimit = 0.0,
        createdAt = now,
        updatedAt = now
    )

    @Test
    fun shouldReturnCachedAccountWhenExists() {
        // GIVEN
        val account = createAccountModel()
        whenever(cachePort.getObject("account:number:$accountNumber", AccountModel::class.java)).thenReturn(account)

        // WHEN
        val result = service.getAccountByAccountNumber(accountNumber)

        // THEN
        assertEquals(account, result)
        verify(accountRepository, never()).findByAccountNumber(any())
    }

    @Test
    fun shouldReturnAccountFromRepositoryAndCacheIt() {
        // GIVEN
        val account = createAccountModel()
        whenever(cachePort.getObject("account:number:$accountNumber", AccountModel::class.java)).thenReturn(null)
        whenever(accountRepository.findByAccountNumber(accountNumber)).thenReturn(account)

        // WHEN
        val result = service.getAccountByAccountNumber(accountNumber)

        // THEN
        assertEquals(account, result)
        verify(cachePort).saveObject(eq("account:number:$accountNumber"), eq(account), any<Duration>())
    }

    @Test
    fun shouldThrowAccountNotFoundExceptionWhenNotExists() {
        // GIVEN
        whenever(cachePort.getObject("account:number:$accountNumber", AccountModel::class.java)).thenReturn(null)
        whenever(accountRepository.findByAccountNumber(accountNumber)).thenReturn(null)

        // WHEN / THEN
        val exception = assertThrows<AccountNotFoundException> {
            service.getAccountByAccountNumber(accountNumber)
        }
        assertEquals("Account with number $accountNumber not found", exception.message)
    }
}

