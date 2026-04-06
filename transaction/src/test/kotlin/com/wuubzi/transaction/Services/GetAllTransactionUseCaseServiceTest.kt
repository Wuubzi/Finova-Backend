package com.wuubzi.transaction.Services

import com.fasterxml.jackson.databind.ObjectMapper
import com.wuubzi.transaction.application.DTOS.Response.AccountResponse
import com.wuubzi.transaction.application.Ports.Out.CachePort
import com.wuubzi.transaction.application.Ports.Out.TransactionRepositoryPort
import com.wuubzi.transaction.application.Ports.Out.WebClientPort
import com.wuubzi.transaction.application.Services.GetAllTransactionUseCaseService
import com.wuubzi.transaction.domain.Models.TransactionModel
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
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class GetAllTransactionUseCaseServiceTest {

    @Mock
    lateinit var transactionRepository: TransactionRepositoryPort

    @Mock
    lateinit var webClient: WebClientPort

    @Mock
    lateinit var cachePort: CachePort

    @Mock
    lateinit var objectMapper: ObjectMapper

    @InjectMocks
    lateinit var service: GetAllTransactionUseCaseService

    private val accountId = UUID.randomUUID()
    private val userId = UUID.randomUUID()
    private val accountNumber = "12345678901234567890"
    private val now = Timestamp.from(Instant.now())

    private fun mockAccount() = AccountResponse(
        accountId = accountId,
        accountNumber = accountNumber,
        userId = userId,
        accountType = "SAVINGS",
        currency = "USD",
        balance = 1000.0,
        availableBalance = 1000.0,
        status = "ACTIVE",
        alias = "Test Account",
        overdraftLimit = 0.0,
        createdAt = now,
        updatedAt = now
    )

    private fun mockTransactionList() = listOf(
        TransactionModel(
            transactionId = UUID.randomUUID(),
            fromAccountId = null,
            toAccountId = accountId,
            amount = 100.0,
            currency = "USD",
            status = "COMPLETED",
            type = "DEPOSIT",
            description = "Deposit",
            createdAt = now
        )
    )

    @Test
    fun shouldThrowWhenAccountNotFound() {
        // GIVEN
        whenever(webClient.getAccountByAccountNumber(accountNumber)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            service.getAllTransactions(accountNumber)
        }
        assertEquals("Account with number $accountNumber not found", exception.message)
    }

    @Test
    fun shouldReturnFromCacheWhenCacheHit() {
        // GIVEN
        val account = mockAccount()
        val cachedJson = "[{\"transactionId\":\"test\"}]"
        val transactions = mockTransactionList()

        whenever(webClient.getAccountByAccountNumber(accountNumber)).thenReturn(account)
        whenever(cachePort.get("transaction:account:$accountId")).thenReturn(cachedJson)
        whenever(objectMapper.readValue(eq(cachedJson), any<com.fasterxml.jackson.core.type.TypeReference<List<TransactionModel>>>()))
            .thenReturn(transactions)

        // WHEN
        val result = service.getAllTransactions(accountNumber)

        // THEN
        assertEquals(transactions, result)
        verify(transactionRepository, never()).findAllByAccountId(any())
    }

    @Test
    fun shouldReturnFromRepoWhenCacheMiss() {
        // GIVEN
        val account = mockAccount()
        val transactions = mockTransactionList()
        val json = "serialized-json"

        whenever(webClient.getAccountByAccountNumber(accountNumber)).thenReturn(account)
        whenever(cachePort.get("transaction:account:$accountId")).thenReturn(null)
        whenever(transactionRepository.findAllByAccountId(accountId)).thenReturn(transactions)
        whenever(objectMapper.writeValueAsString(transactions)).thenReturn(json)

        // WHEN
        val result = service.getAllTransactions(accountNumber)

        // THEN
        assertEquals(transactions, result)
        verify(transactionRepository).findAllByAccountId(accountId)
        verify(cachePort).save(eq("transaction:account:$accountId"), eq(json), any())
    }
}

