package com.wuubzi.account.infrastructure.Adapters

import com.wuubzi.account.domain.models.AccountModel
import com.wuubzi.account.infrastructure.Persistence.Entities.AccountEntity
import com.wuubzi.account.infrastructure.Repositories.AccountRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
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

const val ACCOUNT_ALIAS = "Cuenta de Ahorros"

@ExtendWith(MockitoExtension::class)
class AccountRepositoryAdapterTest {

    @Mock
    lateinit var accountRepository: AccountRepository

    @InjectMocks
    lateinit var adapter: AccountRepositoryAdapter

    private val userId = UUID.randomUUID()
    private val accountId = UUID.randomUUID()
    private val now = Timestamp.from(Instant.now())

    @Test
    fun shouldSaveAccountSuccessfully() {
        // GIVEN
        val domainAccount = AccountModel(
            accountId = accountId,
            accountNumber = "1234567890",
            userId = userId,
            accountType = "SAVINGS",
            currency = "USD",
            balance = 1000.0,
            availableBalance = 1000.0,
            status = "ACTIVE",
            alias = ACCOUNT_ALIAS,
            overdraftLimit = 0.0,
            createdAt = now,
            updatedAt = now
        )

        val entityResponse = AccountEntity()
        entityResponse.idAccount = accountId
        entityResponse.accountNumber = "1234567890"
        entityResponse.userId = userId
        entityResponse.accountType = "SAVINGS"
        entityResponse.currency = "USD"
        entityResponse.balance = 1000.0
        entityResponse.availableBalance = 1000.0
        entityResponse.status = "ACTIVE"
        entityResponse.alias = ACCOUNT_ALIAS
        entityResponse.overdraftLimit = 0.0
        entityResponse.createdAt = now
        entityResponse.updatedAt = now

        whenever(accountRepository.save(any<AccountEntity>())).thenReturn(entityResponse)

        // WHEN
        val result = adapter.save(domainAccount)

        // THEN
        assertEquals(accountId, result.accountId)
        assertEquals("1234567890", result.accountNumber)
        assertEquals(userId, result.userId)
        assertEquals("SAVINGS", result.accountType)
        assertEquals("USD", result.currency)
        assertEquals(1000.0, result.balance)
        assertEquals(1000.0, result.availableBalance)
        assertEquals("ACTIVE", result.status)
        assertEquals(ACCOUNT_ALIAS, result.alias)
        assertEquals(0.0, result.overdraftLimit)
        assertEquals(now, result.createdAt)
        assertEquals(now, result.updatedAt)
        verify(accountRepository).save(any())
    }

    @Test
    fun shouldFindByUserIdSuccessfully() {
        // GIVEN
        val entity = AccountEntity()
        entity.idAccount = accountId
        entity.accountNumber = "0987654321"
        entity.userId = userId
        entity.accountType = "CHECKING"
        entity.currency = "COP"
        entity.balance = 500000.0
        entity.availableBalance = 450000.0
        entity.status = "ACTIVE"
        entity.alias = "Nomina"
        entity.overdraftLimit = 100000.0
        entity.createdAt = now
        entity.updatedAt = now

        whenever(accountRepository.findByUserId(userId)).thenReturn(entity)

        // WHEN
        val result = adapter.findByUserId(userId)

        // THEN
        assertEquals(accountId, result?.accountId)
        assertEquals("0987654321", result?.accountNumber)
        assertEquals(userId, result?.userId)
        assertEquals("CHECKING", result?.accountType)
        assertEquals("COP", result?.currency)
        assertEquals(500000.0, result?.balance)
        assertEquals(now, result?.createdAt)
        verify(accountRepository).findByUserId(userId)
    }

    @Test
    fun shouldReturnNullWhenAccountNotFound() {
        // GIVEN
        whenever(accountRepository.findByUserId(userId)).thenReturn(null)

        // WHEN
        val result = adapter.findByUserId(userId)

        // THEN
        assertNull(result)
        verify(accountRepository).findByUserId(userId)
    }

    @Test
    fun shouldDeleteByUserIdSuccessfully() {
        // WHEN
        adapter.deleteByUserId(userId)

        // THEN
        verify(accountRepository).deleteByUserId(userId)
    }
}