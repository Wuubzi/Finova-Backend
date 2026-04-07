package com.wuubzi.account.Mappers

import com.wuubzi.account.domain.models.AccountModel
import com.wuubzi.account.infrastructure.Persistence.Entities.AccountEntity
import com.wuubzi.account.infrastructure.Persistence.Mappers.toDomain
import com.wuubzi.account.infrastructure.Persistence.Mappers.toEntity
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AccountMapperTest {

    private val accountId = UUID.randomUUID()
    private val userId = UUID.randomUUID()
    private val now = Timestamp.from(Instant.now())

    @Test
    fun shouldMapModelToEntity() {
        // GIVEN
        val model = AccountModel(
            accountId = accountId,
            accountNumber = "12345678901234567890",
            userId = userId,
            accountType = "SAVINGS",
            currency = "USD",
            balance = 1000.0,
            availableBalance = 950.0,
            status = "ACTIVE",
            alias = "My Account",
            overdraftLimit = 100.0,
            createdAt = now,
            updatedAt = now
        )

        // WHEN
        val entity = model.toEntity()

        // THEN
        assertEquals(accountId, entity.idAccount)
        assertEquals("12345678901234567890", entity.accountNumber)
        assertEquals(userId, entity.userId)
        assertEquals("SAVINGS", entity.accountType)
        assertEquals("USD", entity.currency)
        assertEquals(1000.0, entity.balance)
        assertEquals(950.0, entity.availableBalance)
        assertEquals("ACTIVE", entity.status)
        assertEquals("My Account", entity.alias)
        assertEquals(100.0, entity.overdraftLimit)
        assertEquals(now, entity.createdAt)
        assertEquals(now, entity.updatedAt)
    }

    @Test
    fun shouldMapEntityToDomain() {
        // GIVEN
        val entity = AccountEntity().apply {
            idAccount = this@AccountMapperTest.accountId
            accountNumber = "12345678901234567890"
            userId = this@AccountMapperTest.userId
            accountType = "CHECKING"
            currency = "EUR"
            balance = 2000.0
            availableBalance = 1800.0
            status = "BLOCKED"
            alias = "Test Alias"
            overdraftLimit = 200.0
            createdAt = now
            updatedAt = now
        }

        // WHEN
        val model = entity.toDomain()

        // THEN
        assertEquals(accountId, model.accountId)
        assertEquals("12345678901234567890", model.accountNumber)
        assertEquals(userId, model.userId)
        assertEquals("CHECKING", model.accountType)
        assertEquals("EUR", model.currency)
        assertEquals(2000.0, model.balance)
        assertEquals(1800.0, model.availableBalance)
        assertEquals("BLOCKED", model.status)
        assertEquals("Test Alias", model.alias)
        assertEquals(200.0, model.overdraftLimit)
        assertEquals(now, model.createdAt)
        assertEquals(now, model.updatedAt)
    }

    @Test
    fun shouldMapEntityWithNullUpdatedAtToDomainWithDefaultTimestamp() {
        // GIVEN
        val entity = AccountEntity().apply {
            idAccount = this@AccountMapperTest.accountId
            accountNumber = "12345678901234567890"
            userId = this@AccountMapperTest.userId
            accountType = "SAVINGS"
            currency = "USD"
            balance = 500.0
            availableBalance = 500.0
            status = "ACTIVE"
            alias = "Savings"
            overdraftLimit = 0.0
            createdAt = now
            updatedAt = null
        }

        // WHEN
        val model = entity.toDomain()

        // THEN
        assertNotNull(model.updatedAt)
        assertEquals(accountId, model.accountId)
    }
}

