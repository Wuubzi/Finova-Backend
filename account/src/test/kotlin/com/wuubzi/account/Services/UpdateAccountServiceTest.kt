package com.wuubzi.account.application.Services

import com.wuubzi.account.application.DTOS.Request.AccountRequestDTO
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
import java.util.*

@ExtendWith(MockitoExtension::class)
class UpdateAccountServiceTest {

    @Mock
    lateinit var accountRepository: AccountRepositoryPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var updateAccountService: UpdateAccountService

    private val userId = UUID.randomUUID()

    @Test
    fun shouldUpdateAccountSuccessfully() {
        // GIVEN: Una cuenta existente y un DTO con datos nuevos
        val existingAccount = AccountModel(
            accountId = UUID.randomUUID(),
            accountNumber = "123456",
            userId = userId,
            accountType = "SAVINGS",
            currency = "USD",
            balance = 1000.0,
            availableBalance = 1000.0,
            status = "ACTIVE",
            alias = "Antiguo Alias",
            overdraftLimit = 0.0,
            createdAt = mock(),
            updatedAt = mock()
        )

        val updateRequest = AccountRequestDTO(
            accountType = "CHECKING",
            currency = "EUR",
            balance = 2000.0,
            availableBalance = 1800.0,
            status = "ACTIVE",
            alias = "Nuevo Alias",
            overdraftLimit = 500.0
        )

        whenever(accountRepository.findByUserId(userId)).thenReturn(existingAccount)
        whenever(accountRepository.save(any())).thenAnswer { it.arguments[0] as AccountModel }

        // WHEN
        updateAccountService.updateAccount(userId, updateRequest)

        // THEN: Verificamos que el save reciba el modelo con los datos actualizados
        verify(accountRepository).save(argThat {
            this.accountType == "CHECKING" &&
                    this.currency == "EUR" &&
                    this.balance == 2000.0 &&
                    this.alias == "Nuevo Alias" &&
                    this.overdraftLimit == 500.0 &&
                    this.accountNumber == "123456" // El número de cuenta NO debe cambiar
        })
    }

    @Test
    fun shouldThrowExceptionWhenAccountToUpdateNotFound() {
        // GIVEN
        val updateRequest = mock<AccountRequestDTO>()
        whenever(accountRepository.findByUserId(userId)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            updateAccountService.updateAccount(userId, updateRequest)
        }

        assertEquals("Account with user id $userId not found", exception.message)
        verify(accountRepository, never()).save(any())
    }
}