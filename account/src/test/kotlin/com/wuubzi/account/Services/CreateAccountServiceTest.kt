package com.wuubzi.account.application.Services

import com.wuubzi.account.application.DTOS.Request.AccountRequestDTO
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.application.Ports.out.CachePort
import com.wuubzi.account.application.Ports.out.UserCacheRepositoryPort
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
class CreateAccountServiceTest {

    @Mock
    lateinit var accountRepository: AccountRepositoryPort

    @Mock
    lateinit var userCacheRepository: UserCacheRepositoryPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var createAccountService: CreateAccountService

    private val userId = UUID.randomUUID()

    @Test
    fun shouldCreateAccountSuccessfully() {
        // GIVEN
        val requestDTO = AccountRequestDTO(
            accountType = "SAVINGS",
            currency = "COP",
            balance = 0.0,
            availableBalance = 0.0,
            status = "ACTIVE",
            alias = "Mi cuenta principal",
            overdraftLimit = 0.0
        )

        // Mockeamos que el usuario SÍ existe en caché y NO tiene cuenta aún
        whenever(userCacheRepository.findByUserId(userId)).thenReturn(mock())
        whenever(accountRepository.findByUserId(userId)).thenReturn(null)
        whenever(accountRepository.save(any())).thenAnswer { it.arguments[0] as AccountModel }

        // WHEN
        createAccountService.createAccount(userId, requestDTO)

        // THEN: Verificamos que se guardó un modelo con los datos correctos
        verify(accountRepository).save(argThat {
            this.userId == userId &&
                    this.accountType == "SAVINGS" &&
                    this.accountNumber.length == 20 // Validamos la lógica privada de generación
        })
    }

    @Test
    fun shouldThrowExceptionWhenUserNotInCache() {
        // GIVEN
        val requestDTO = mock<AccountRequestDTO>()
        whenever(userCacheRepository.findByUserId(userId)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            createAccountService.createAccount(userId, requestDTO)
        }

        assertEquals("User with id $userId not found", exception.message)
        verify(accountRepository, never()).save(any())
    }

    @Test
    fun shouldThrowExceptionWhenUserAlreadyHasAccount() {
        // GIVEN
        val requestDTO = mock<AccountRequestDTO>()
        val existingAccount = mock<AccountModel>()

        whenever(userCacheRepository.findByUserId(userId)).thenReturn(mock())
        whenever(accountRepository.findByUserId(userId)).thenReturn(existingAccount)

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            createAccountService.createAccount(userId, requestDTO)
        }

        assertEquals("User with id $userId already has an account", exception.message)
        verify(accountRepository, never()).save(any())
    }
}