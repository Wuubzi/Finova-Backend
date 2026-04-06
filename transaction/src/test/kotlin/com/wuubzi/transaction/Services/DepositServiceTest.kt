package com.wuubzi.transaction.Services

import com.wuubzi.transaction.application.DTOS.Response.AccountResponse
import com.wuubzi.transaction.application.Ports.Out.CachePort
import com.wuubzi.transaction.application.Ports.Out.KafkaPort
import com.wuubzi.transaction.application.Ports.Out.TransactionRepositoryPort
import com.wuubzi.transaction.application.Ports.Out.WebClientPort
import com.wuubzi.transaction.application.Services.DepositService
import com.wuubzi.transaction.domain.Models.TransactionModel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class DepositServiceTest {

    @Mock
    lateinit var transactionRepository: TransactionRepositoryPort

    @Mock
    lateinit var kafka: KafkaPort

    @Mock
    lateinit var webClient: WebClientPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var depositService: DepositService

    private val accountId = UUID.randomUUID()
    private val userId = UUID.randomUUID()
    private val accountNumber = "12345678901234567890"
    private val now = Timestamp.from(Instant.now())

    private fun mockAccount(status: String = "ACTIVE") = AccountResponse(
        accountId = accountId,
        accountNumber = accountNumber,
        userId = userId,
        accountType = "SAVINGS",
        currency = "USD",
        balance = 1000.0,
        availableBalance = 1000.0,
        status = status,
        alias = "Test Account",
        overdraftLimit = 0.0,
        createdAt = now,
        updatedAt = now
    )

    private fun mockTransactionModel() = TransactionModel(
        transactionId = UUID.randomUUID(),
        fromAccountId = null,
        toAccountId = accountId,
        amount = 500.0,
        currency = "USD",
        status = "PENDING",
        type = "DEPOSIT",
        description = "Deposit to account $accountNumber",
        createdAt = now
    )

    @Test
    fun shouldDepositSuccessfully() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(accountNumber)).thenReturn(mockAccount())
        whenever(transactionRepository.save(any())).thenReturn(mockTransactionModel())

        // WHEN
        depositService.deposit(accountNumber, 500.0, "test@email.com")

        // THEN
        verify(webClient).getAccountByAccountNumber(accountNumber)
        verify(transactionRepository).save(any())
        verify(cachePort).delete("transaction:account:$accountId")
        verify(kafka).publishTransactionEvent(any())
    }

    @Test
    fun shouldThrowWhenAccountNotFound() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(accountNumber)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            depositService.deposit(accountNumber, 500.0, "test@email.com")
        }
        assertEquals("Account with number $accountNumber not found", exception.message)
    }

    @Test
    fun shouldThrowWhenAccountNotActive() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(accountNumber)).thenReturn(mockAccount("BLOCKED"))

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            depositService.deposit(accountNumber, 500.0, "test@email.com")
        }
        assertEquals("Account is not active", exception.message)
    }
}

