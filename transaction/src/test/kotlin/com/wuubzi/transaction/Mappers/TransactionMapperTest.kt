package com.wuubzi.transaction.Mappers

import com.wuubzi.transaction.domain.Models.TransactionModel
import com.wuubzi.transaction.infrastructure.Persistence.Entities.TransactionEntity
import com.wuubzi.transaction.infrastructure.Persistence.Mappers.toDomain
import com.wuubzi.transaction.infrastructure.Persistence.Mappers.toEntity
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TransactionMapperTest {

    private val transactionId = UUID.randomUUID()
    private val fromAccountId = UUID.randomUUID()
    private val toAccountId = UUID.randomUUID()
    private val now = Timestamp.from(Instant.now())

    @Test
    fun shouldMapModelToEntity() {
        // GIVEN
        val model = TransactionModel(
            transactionId = transactionId,
            fromAccountId = fromAccountId,
            toAccountId = toAccountId,
            amount = 100.0,
            currency = "USD",
            status = "PENDING",
            type = "TRANSFER",
            description = "Test transfer",
            createdAt = now
        )

        // WHEN
        val entity = model.toEntity()

        // THEN
        assertEquals(transactionId, entity.transactionId)
        assertEquals(fromAccountId, entity.fromAccountId)
        assertEquals(toAccountId, entity.toAccountId)
        assertEquals(100.0, entity.amount)
        assertEquals("USD", entity.currency)
        assertEquals("PENDING", entity.status)
        assertEquals("TRANSFER", entity.type)
        assertEquals("Test transfer", entity.description)
        assertEquals(now, entity.createdAt)
    }

    @Test
    fun shouldMapEntityToDomain() {
        // GIVEN
        val entity = TransactionEntity().apply {
            transactionId = this@TransactionMapperTest.transactionId
            fromAccountId = this@TransactionMapperTest.fromAccountId
            toAccountId = this@TransactionMapperTest.toAccountId
            amount = 250.0
            currency = "EUR"
            status = "COMPLETED"
            type = "DEPOSIT"
            description = "Test deposit"
            createdAt = now
        }

        // WHEN
        val model = entity.toDomain()

        // THEN
        assertEquals(transactionId, model.transactionId)
        assertEquals(fromAccountId, model.fromAccountId)
        assertEquals(toAccountId, model.toAccountId)
        assertEquals(250.0, model.amount)
        assertEquals("EUR", model.currency)
        assertEquals("COMPLETED", model.status)
        assertEquals("DEPOSIT", model.type)
        assertEquals("Test deposit", model.description)
        assertEquals(now, model.createdAt)
    }

    @Test
    fun shouldMapEntityWithNullsToDomainWithDefaults() {
        // GIVEN
        val entity = TransactionEntity().apply {
            transactionId = null
            fromAccountId = null
            toAccountId = null
            amount = null
            currency = null
            status = null
            type = null
            description = null
            createdAt = null
        }

        // WHEN
        val model = entity.toDomain()

        // THEN
        assertNotNull(model.transactionId)
        assertEquals(null, model.fromAccountId)
        assertEquals(null, model.toAccountId)
        assertEquals(0.0, model.amount)
        assertEquals("USD", model.currency)
        assertEquals("PENDING", model.status)
        assertEquals("TRANSFER", model.type)
        assertEquals("", model.description)
        assertNotNull(model.createdAt)
    }

    @Test
    fun shouldMapModelWithNullAccountIdsToEntity() {
        // GIVEN
        val model = TransactionModel(
            transactionId = transactionId,
            fromAccountId = null,
            toAccountId = null,
            amount = 50.0,
            currency = "USD",
            status = "PENDING",
            type = "DEPOSIT",
            description = "Deposit test",
            createdAt = now
        )

        // WHEN
        val entity = model.toEntity()

        // THEN
        assertEquals(transactionId, entity.transactionId)
        assertEquals(null, entity.fromAccountId)
        assertEquals(null, entity.toAccountId)
        assertEquals(50.0, entity.amount)
    }
}

