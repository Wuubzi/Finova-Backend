package com.wuubzi.transaction.Adapters

import com.wuubzi.transaction.infrastructure.Adapters.TransactionRepositoryAdapter
import com.wuubzi.transaction.infrastructure.Persistence.Entities.TransactionEntity
import com.wuubzi.transaction.infrastructure.Repository.TransactionRepository
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
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class TransactionRepositoryAdapterTest {

    @Mock
    lateinit var transactionRepository: TransactionRepository

    @InjectMocks
    lateinit var adapter: TransactionRepositoryAdapter

    private val transactionId = UUID.randomUUID()
    private val fromAccountId = UUID.randomUUID()
    private val toAccountId = UUID.randomUUID()
    private val now = Timestamp.from(Instant.now())

    private fun mockEntity(): TransactionEntity {
        val entity = TransactionEntity()
        entity.transactionId = transactionId
        entity.fromAccountId = fromAccountId
        entity.toAccountId = toAccountId
        entity.amount = 500.0
        entity.currency = "USD"
        entity.status = "PENDING"
        entity.type = "TRANSFER"
        entity.description = "Test transfer"
        entity.createdAt = now
        return entity
    }

    private fun mockDomainModel() = TransactionModel(
        transactionId = transactionId,
        fromAccountId = fromAccountId,
        toAccountId = toAccountId,
        amount = 500.0,
        currency = "USD",
        status = "PENDING",
        type = "TRANSFER",
        description = "Test transfer",
        createdAt = now
    )

    @Test
    fun shouldSaveSuccessfully() {
        // GIVEN
        val entity = mockEntity()
        whenever(transactionRepository.save(any<TransactionEntity>())).thenReturn(entity)

        // WHEN
        val result = adapter.save(mockDomainModel())

        // THEN
        assertNotNull(result)
        assertEquals(transactionId, result.transactionId)
        assertEquals(500.0, result.amount)
        assertEquals("PENDING", result.status)
        verify(transactionRepository).save(any<TransactionEntity>())
    }

    @Test
    fun shouldFindByIdSuccessfully() {
        // GIVEN
        val entity = mockEntity()
        whenever(transactionRepository.findById(transactionId)).thenReturn(Optional.of(entity))

        // WHEN
        val result = adapter.findById(transactionId)

        // THEN
        assertNotNull(result)
        assertEquals(transactionId, result.transactionId)
        assertEquals("USD", result.currency)
    }

    @Test
    fun shouldReturnNullWhenNotFoundById() {
        // GIVEN
        whenever(transactionRepository.findById(transactionId)).thenReturn(Optional.empty())

        // WHEN
        val result = adapter.findById(transactionId)

        // THEN
        assertNull(result)
    }

    @Test
    fun shouldFindAllByAccountId() {
        // GIVEN
        val entity = mockEntity()
        whenever(transactionRepository.findByFromAccountIdOrToAccountId(fromAccountId, fromAccountId))
            .thenReturn(listOf(entity))

        // WHEN
        val result = adapter.findAllByAccountId(fromAccountId)

        // THEN
        assertEquals(1, result.size)
        assertEquals(transactionId, result[0].transactionId)
    }

    @Test
    fun shouldUpdateStatusSuccessfully() {
        // GIVEN
        val entity = mockEntity()
        whenever(transactionRepository.findById(transactionId)).thenReturn(Optional.of(entity))
        whenever(transactionRepository.save(any<TransactionEntity>())).thenReturn(entity)

        // WHEN
        adapter.updateStatus(transactionId, "COMPLETED")

        // THEN
        assertEquals("COMPLETED", entity.status)
        verify(transactionRepository).save(entity)
    }

    @Test
    fun shouldThrowWhenUpdateStatusTransactionNotFound() {
        // GIVEN
        whenever(transactionRepository.findById(transactionId)).thenReturn(Optional.empty())

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            adapter.updateStatus(transactionId, "COMPLETED")
        }
        assertEquals("Transaction with id $transactionId not found", exception.message)
    }
}

