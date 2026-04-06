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
import org.mockito.kotlin.*
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UnBlockAccountServiceTest {

    @Mock
    lateinit var accountRepository: AccountRepositoryPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var unBlockAccountService: UnBlockAccountService

    private val userId = UUID.randomUUID()

    // Helper para crear un modelo base
    private fun createMockAccount(status: String) = AccountModel(
        accountId = UUID.randomUUID(),
        accountNumber = "123456",
        userId = userId,
        accountType = "SAVINGS",
        currency = "USD",
        balance = 1000.0,
        availableBalance = 1000.0,
        status = status,
        alias = "Cuenta Test",
        overdraftLimit = 0.0,
        createdAt = mock(),
        updatedAt = mock()
    )

    @Test
    fun shouldUnblockAccountSuccessfully() {
        // GIVEN: Una cuenta que está bloqueada
        val blockedAccount = createMockAccount(status = "BLOCKED")
        val activeAccount = blockedAccount.copy(status = "ACTIVE")
        whenever(accountRepository.findByUserId(userId)).thenReturn(blockedAccount)
        whenever(accountRepository.save(argThat { this.status == "ACTIVE" })).thenReturn(activeAccount)

        // WHEN
        unBlockAccountService.unBlockAccount(userId)

        // THEN: Verificamos que se guarde con el estado ACTIVE
        verify(accountRepository).save(argThat {
            this.status == "ACTIVE" && this.userId == userId
        })
    }

    @Test
    fun shouldThrowExceptionWhenAccountNotFound() {
        // GIVEN
        whenever(accountRepository.findByUserId(userId)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            unBlockAccountService.unBlockAccount(userId)
        }

        assertEquals("User with id $userId not found", exception.message)
        verify(accountRepository, never()).save(any())
    }

    @Test
    fun shouldThrowExceptionWhenAccountIsAlreadyActive() {
        // GIVEN: La cuenta ya está activa
        val activeAccount = createMockAccount(status = "ACTIVE")
        whenever(accountRepository.findByUserId(userId)).thenReturn(activeAccount)

        // WHEN & THEN: El 'require' lanza la excepción
        val exception = assertThrows<IllegalArgumentException> {
            unBlockAccountService.unBlockAccount(userId)
        }

        assertEquals("User with id $userId already active", exception.message)
        verify(accountRepository, never()).save(any())
    }
}