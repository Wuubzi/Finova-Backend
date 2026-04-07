package com.wuubzi.transaction.Services

import com.wuubzi.transaction.application.DTOS.Response.AccountResponse
import com.wuubzi.transaction.application.Ports.Out.CachePort
import com.wuubzi.transaction.application.Ports.Out.KafkaPort
import com.wuubzi.transaction.application.Ports.Out.TransactionRepositoryPort
import com.wuubzi.transaction.application.Ports.Out.WebClientPort
import com.wuubzi.transaction.application.Services.TransferService
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
class TransferServiceTest {

    @Mock
    lateinit var transactionRepository: TransactionRepositoryPort

    @Mock
    lateinit var webClient: WebClientPort

    @Mock
    lateinit var kafkaPort: KafkaPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var transferService: TransferService

    private val fromAccountId = UUID.randomUUID()
    private val toAccountId = UUID.randomUUID()
    private val userId = UUID.randomUUID()
    private val fromAccountNumber = "11111111111111111111"
    private val toAccountNumber = "22222222222222222222"
    private val now = Timestamp.from(Instant.now())

    companion object {
        private const val TEST_EMAIL = "test@email.com"
    }

    private fun mockAccount(
        id: UUID = fromAccountId,
        number: String = fromAccountNumber,
        status: String = "ACTIVE",
        balance: Double = 1000.0,
        currency: String = "USD"
    ) = AccountResponse(
        accountId = id,
        accountNumber = number,
        userId = userId,
        accountType = "SAVINGS",
        currency = currency,
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
        fromAccountId = fromAccountId,
        toAccountId = toAccountId,
        amount = 300.0,
        currency = "USD",
        status = "PENDING",
        type = "TRANSFER",
        description = "Transfer from $fromAccountNumber to $toAccountNumber",
        createdAt = now
    )

    @Test
    fun shouldTransferSuccessfully() {
        // GIVEN
        val fromAccount = mockAccount(id = fromAccountId, number = fromAccountNumber)
        val toAccount = mockAccount(id = toAccountId, number = toAccountNumber)
        whenever(webClient.getAccountByAccountNumber(fromAccountNumber)).thenReturn(fromAccount)
        whenever(webClient.getAccountByAccountNumber(toAccountNumber)).thenReturn(toAccount)
        whenever(transactionRepository.save(any())).thenReturn(mockTransactionModel())

        // WHEN
        transferService.transfer(fromAccountNumber, toAccountNumber, 300.0, TEST_EMAIL)

        // THEN
        verify(transactionRepository).save(any())
        verify(cachePort).delete("transaction:account:$fromAccountId")
        verify(cachePort).delete("transaction:account:$toAccountId")
        verify(kafkaPort).publishTransactionEvent(any())
    }

    @Test
    fun shouldThrowWhenSameAccount() {
        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            transferService.transfer(fromAccountNumber, fromAccountNumber, 300.0, TEST_EMAIL)
        }
        assertEquals("Cannot transfer to the same account", exception.message)
    }

    @Test
    fun shouldThrowWhenSourceAccountNotFound() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(fromAccountNumber)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            transferService.transfer(fromAccountNumber, toAccountNumber, 300.0, TEST_EMAIL)
        }
        assertEquals("Source account with number $fromAccountNumber not found", exception.message)
    }

    @Test
    fun shouldThrowWhenDestinationAccountNotFound() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(fromAccountNumber)).thenReturn(mockAccount())
        whenever(webClient.getAccountByAccountNumber(toAccountNumber)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            transferService.transfer(fromAccountNumber, toAccountNumber, 300.0, TEST_EMAIL)
        }
        assertEquals("Destination account with number $toAccountNumber not found", exception.message)
    }

    @Test
    fun shouldThrowWhenSourceAccountNotActive() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(fromAccountNumber))
            .thenReturn(mockAccount(status = "BLOCKED"))
        whenever(webClient.getAccountByAccountNumber(toAccountNumber))
            .thenReturn(mockAccount(id = toAccountId, number = toAccountNumber))

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            transferService.transfer(fromAccountNumber, toAccountNumber, 300.0, TEST_EMAIL)
        }
        assertEquals("Source account is not active", exception.message)
    }

    @Test
    fun shouldThrowWhenDestinationAccountNotActive() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(fromAccountNumber))
            .thenReturn(mockAccount())
        whenever(webClient.getAccountByAccountNumber(toAccountNumber))
            .thenReturn(mockAccount(id = toAccountId, number = toAccountNumber, status = "BLOCKED"))

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            transferService.transfer(fromAccountNumber, toAccountNumber, 300.0, TEST_EMAIL)
        }
        assertEquals("Destination account is not active", exception.message)
    }

    @Test
    fun shouldThrowWhenCurrencyMismatch() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(fromAccountNumber))
            .thenReturn(mockAccount(currency = "USD"))
        whenever(webClient.getAccountByAccountNumber(toAccountNumber))
            .thenReturn(mockAccount(id = toAccountId, number = toAccountNumber, currency = "EUR"))

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            transferService.transfer(fromAccountNumber, toAccountNumber, 300.0, TEST_EMAIL)
        }
        assertEquals("Currency mismatch between accounts", exception.message)
    }

    @Test
    fun shouldThrowWhenInsufficientBalance() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(fromAccountNumber))
            .thenReturn(mockAccount(balance = 50.0))
        whenever(webClient.getAccountByAccountNumber(toAccountNumber))
            .thenReturn(mockAccount(id = toAccountId, number = toAccountNumber))

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            transferService.transfer(fromAccountNumber, toAccountNumber, 300.0, TEST_EMAIL)
        }
        assertEquals("Insufficient balance", exception.message)
        verify(transactionRepository, never()).save(any())
    }
}

