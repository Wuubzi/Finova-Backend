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
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class DeleteAccountServiceTest {

    @Mock
    lateinit var accountRepository: AccountRepositoryPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var deleteAccountService: DeleteAccountService

    private val userId = UUID.randomUUID()

    @Test
    fun shouldDeleteAccountSuccessfully() {
        // GIVEN: Simulamos que la cuenta existe devolviendo un mock del modelo
        val existingAccount = mock<AccountModel>()
        whenever(accountRepository.findByUserId(userId)).thenReturn(existingAccount)

        // WHEN
        deleteAccountService.deleteAccount(userId)

        // THEN: Verificamos que se llamó al borrado
        verify(accountRepository).findByUserId(userId)
        verify(accountRepository).deleteByUserId(userId)
    }

    @Test
    fun shouldThrowExceptionWhenAccountToDeleteNotFound() {
        // GIVEN: El repositorio devuelve null (no existe la cuenta)
        whenever(accountRepository.findByUserId(userId)).thenReturn(null)

        // WHEN & THEN: Verificamos la excepción y el mensaje
        val exception = assertThrows<IllegalArgumentException> {
            deleteAccountService.deleteAccount(userId)
        }

        assertEquals("Account with user id $userId not found", exception.message)

        // CRÍTICO: Aseguramos que NUNCA se intentó borrar nada si no existía
        verify(accountRepository, never()).deleteByUserId(userId)
    }
}