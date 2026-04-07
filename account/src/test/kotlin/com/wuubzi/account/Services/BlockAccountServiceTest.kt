package com.wuubzi.account.application.Services

import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.application.Ports.out.CachePort
import com.wuubzi.account.domain.models.AccountModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class BlockAccountServiceTest {

    @Mock
    lateinit var accountRepository: AccountRepositoryPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var blockAccountService: BlockAccountService

    private val userId = UUID.randomUUID()
    private val now = Timestamp.from(Instant.now())

    // Helper para crear un modelo de cuenta rápido
    private fun createMockAccount(status: String) = AccountModel(
        accountId = UUID.randomUUID(),
        accountNumber = "123456",
        userId = userId,
        accountType = "SAVINGS",
        currency = "USD",
        balance = 1000.0,
        availableBalance = 1000.0,
        status = status,
        alias = "Cuenta de Carlos",
        overdraftLimit = 0.0,
        createdAt = now,
        updatedAt = now
    )

    @Test
    fun shouldBlockAccountSuccessfully() {
        // GIVEN: Una cuenta activa
        val existingAccount = createMockAccount(status = "ACTIVE")
        val blockedAccount = existingAccount.copy(status = "BLOCKED")
        whenever(accountRepository.findByUserId(userId)).thenReturn(existingAccount)
        whenever(accountRepository.save(argThat { this.status == "BLOCKED" })).thenReturn(blockedAccount)

        // WHEN
        blockAccountService.blockAccount(userId)

        // THEN: Verificamos que se guardó con el estado BLOCKED
        verify(accountRepository).save(argThat {
            this.userId == userId && this.status == "BLOCKED"
        })
    }

    @Test
    fun shouldThrowExceptionWhenAccountNotFound() {
        // GIVEN: El repositorio no devuelve nada
        whenever(accountRepository.findByUserId(userId)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            blockAccountService.blockAccount(userId)
        }

        assertEquals("User with id $userId not found", exception.message)
        verify(accountRepository, never()).save(any())
    }

    @Test
    fun shouldThrowExceptionWhenAccountAlreadyBlocked() {
        // GIVEN: La cuenta ya está bloqueada
        val blockedAccount = createMockAccount(status = "BLOCKED")
        whenever(accountRepository.findByUserId(userId)).thenReturn(blockedAccount)

        // WHEN & THEN: El 'require' de Kotlin lanza IllegalArgumentException
        val exception = assertThrows<IllegalArgumentException> {
            blockAccountService.blockAccount(userId)
        }

        assertEquals("User with id $userId already blocked", exception.message)
        verify(accountRepository, never()).save(any())
    }
}