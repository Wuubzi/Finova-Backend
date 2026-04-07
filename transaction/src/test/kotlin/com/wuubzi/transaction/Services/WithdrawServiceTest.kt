package com.wuubzi.transaction.Services

import com.wuubzi.transaction.application.DTOS.Response.AccountResponse
import com.wuubzi.transaction.application.Ports.Out.CachePort
import com.wuubzi.transaction.application.Ports.Out.KafkaPort
import com.wuubzi.transaction.application.Ports.Out.TransactionRepositoryPort
import com.wuubzi.transaction.application.Ports.Out.WebClientPort
import com.wuubzi.transaction.application.Services.WithdrawService
import com.wuubzi.transaction.domain.Models.TransactionModel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class WithdrawServiceTest {

    @Mock
    lateinit var transactionRepository: TransactionRepositoryPort

    @Mock
    lateinit var webClient: WebClientPort

    @Mock
    lateinit var kafkaPort: KafkaPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var withdrawService: WithdrawService

    private val accountId = UUID.randomUUID()
    private val userId = UUID.randomUUID()
    private val accountNumber = "12345678901234567890"
    private val now = Timestamp.from(Instant.now())

    companion object {
        private const val TEST_EMAIL = "test@email.com"
    }

    private fun mockAccount(status: String = "ACTIVE", balance: Double = 1000.0) = AccountResponse(
        accountId = accountId,
        accountNumber = accountNumber,
        userId = userId,
        accountType = "SAVINGS",
        currency = "USD",
        balance = balance,
        availableBalance = balance,
        status = status,
        alias = "Test Account",
        overdraftLimit = 0.0,
        createdAt = now,
        updatedAt = now
    )

    private fun mockTransactionModel() = TransactionModel(
        transactionId = UUID.randomUUID(),
        fromAccountId = accountId,
        toAccountId = null,
        amount = 200.0,
        currency = "USD",
        status = "PENDING",
        type = "WITHDRAW",
        description = "Withdrawal from account $accountNumber",
        createdAt = now
    )

    @Test
    fun shouldWithdrawSuccessfully() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(accountNumber)).thenReturn(mockAccount())
        whenever(transactionRepository.save(any())).thenReturn(mockTransactionModel())

        // WHEN
        withdrawService.withdraw(accountNumber, 200.0, TEST_EMAIL)

        // THEN
        verify(webClient).getAccountByAccountNumber(accountNumber)
        verify(transactionRepository).save(any())
        verify(cachePort).delete("transaction:account:$accountId")
        verify(kafkaPort).publishTransactionEvent(any())
    }

    @Test
    fun shouldThrowWhenAmountIsZeroOrNegative() {
        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            withdrawService.withdraw(accountNumber, 0.0, TEST_EMAIL)
        }
        assertEquals("Withdraw amount must be greater than zero", exception.message)
    }

    @Test
    fun shouldThrowWhenAccountNotFound() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(accountNumber)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            withdrawService.withdraw(accountNumber, 200.0, TEST_EMAIL)
        }
        assertEquals("Account with number $accountNumber not found", exception.message)
    }

    @Test
    fun shouldThrowWhenAccountNotActive() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(accountNumber)).thenReturn(mockAccount("BLOCKED"))

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            withdrawService.withdraw(accountNumber, 200.0, TEST_EMAIL)
        }
        assertEquals("Account is not active", exception.message)
    }

    @Test
    fun shouldThrowWhenInsufficientBalance() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(accountNumber)).thenReturn(mockAccount(balance = 50.0))

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            withdrawService.withdraw(accountNumber, 200.0, TEST_EMAIL)
        }
        assertEquals("Insufficient balance", exception.message)
        verify(transactionRepository, never()).save(any())
    }
}

