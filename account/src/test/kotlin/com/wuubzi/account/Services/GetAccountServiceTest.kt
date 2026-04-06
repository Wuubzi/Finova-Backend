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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class GetAccountServiceTest {

    @Mock
    lateinit var accountRepository: AccountRepositoryPort

    @Mock
    lateinit var cachePort: CachePort

    @InjectMocks
    lateinit var getAccountService: GetAccountService

    private val userId = UUID.randomUUID()

    @Test
    fun shouldReturnAccountSuccessfully() {
        // GIVEN
        val mockAccount = mock<AccountModel>()
        whenever(accountRepository.findByUserId(userId)).thenReturn(mockAccount)

        // WHEN
        val result = getAccountService.getAccount(userId)

        // THEN
        assertEquals(mockAccount, result)
        verify(accountRepository).findByUserId(userId)
    }

    @Test
    fun shouldThrowExceptionWhenAccountNotFound() {
        // GIVEN
        whenever(accountRepository.findByUserId(userId)).thenReturn(null)

        // WHEN & THEN
        val exception = assertThrows<IllegalArgumentException> {
            getAccountService.getAccount(userId)
        }

        assertEquals("This user id $userId dont have any account", exception.message)
        verify(accountRepository).findByUserId(userId)
    }
}