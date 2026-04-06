package com.wuubzi.transaction.Services

import com.wuubzi.transaction.application.Ports.Out.CachePort
import com.wuubzi.transaction.application.Ports.Out.TransactionRepositoryPort
import com.wuubzi.transaction.application.Services.GetTransactionService
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
class GetTransactionServiceTest {

    @Mock
    lateinit var transactionRepository: TransactionRepositoryPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var getTransactionService: GetTransactionService

    private val transactionId = UUID.randomUUID()
    private val now = Timestamp.from(Instant.now())

    private fun mockTransaction() = TransactionModel(
        transactionId = transactionId,
        fromAccountId = UUID.randomUUID(),
        toAccountId = UUID.randomUUID(),
        amount = 100.0,
        currency = "USD",
        status = "COMPLETED",
        type = "DEPOSIT",
        description = "Test deposit",
        createdAt = now
    )

    @Test
    fun shouldReturnTransactionFromCache() {
        // GIVEN
        val transaction = mockTransaction()
        whenever(cachePort.getObject("transaction:$transactionId", TransactionModel::class.java))
            .thenReturn(transaction)

        // WHEN
        val result = getTransactionService.getTransaction(transactionId)

        // THEN
        assertEquals(transaction, result)
        verify(transactionRepository, never()).findById(any())
    }

    @Test
    fun shouldReturnTransactionFromRepoAndCacheIt() {
        // GIVEN
        val transaction = mockTransaction()
        whenever(cachePort.getObject("transaction:$transactionId", TransactionModel::class.java))
            .thenReturn(null)
        whenever(transactionRepository.findById(transactionId)).thenReturn(transaction)

        // WHEN
        val result = getTransactionService.getTransaction(transactionId)

        // THEN
        assertEquals(transaction, result)
        verify(transactionRepository).findById(transactionId)
        verify(cachePort).saveObject(eq("transaction:$transactionId"), eq(transaction), any())
    }

    @Test
    fun shouldThrowWhenTransactionNotFound() {
        // GIVEN
        whenever(cachePort.getObject("transaction:$transactionId", TransactionModel::class.java))
            .thenReturn(null)
        whenever(transactionRepository.findById(transactionId)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            getTransactionService.getTransaction(transactionId)
        }
        assertEquals("Transaction with id $transactionId not found", exception.message)
    }
}

