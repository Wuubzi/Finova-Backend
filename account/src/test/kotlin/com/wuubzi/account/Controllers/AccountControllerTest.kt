package com.wuubzi.account.infrastructure.Controllers

import com.wuubzi.account.application.DTOS.Request.AccountRequestDTO
import com.wuubzi.account.application.DTOS.Response.AccountBalanceResponse
import com.wuubzi.account.application.Ports.`in`.*
import com.wuubzi.account.domain.models.AccountModel
import com.wuubzi.account.utils.DateFormatter
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class AccountControllerTest {

    @Mock
    lateinit var createAccountUseCase: CreateAccountUseCase

    @Mock
    lateinit var blockAccountUseCase: BlockAccountUseCase

    @Mock
    lateinit var unblockAccountUseCase: UnBlockAccountUseCase

    @Mock
    lateinit var getBalanceAccountUseCase: GetBalanceAccountUseCase

    @Mock
    lateinit var updateAccountUseCase: UpdateAccountUseCase

    @Mock
    lateinit var getAccountUseCase: GetAccountUseCase

    @Mock
    lateinit var deleteAccountUseCase: DeleteAccountUseCase

    @Mock
    lateinit var dateFormatter: DateFormatter

    @Mock
    lateinit var request: HttpServletRequest

    @InjectMocks
    lateinit var accountController: AccountController

    private val userId = UUID.randomUUID()
    private val accountId = UUID.randomUUID()
    private val now = Timestamp.from(Instant.now())
    private val mockDate = "2026-03-25 17:00:00"
    private val mockUrl = "http://localhost/api/v1/account"

    @Test
    fun shouldGetAccountSuccessfully() {
        // GIVEN
        val mockAccount = AccountModel(
            accountId = accountId,
            accountNumber = "12345",
            userId = userId,
            accountType = "SAVINGS",
            currency = "USD",
            balance = 1000.0,
            availableBalance = 1000.0,
            status = "ACTIVE",
            alias = "Ahorros",
            overdraftLimit = 0.0,
            createdAt = now,
            updatedAt = now
        )
        whenever(getAccountUseCase.getAccount(userId)).thenReturn(mockAccount)

        // WHEN
        val response = accountController.getAccount(userId)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockAccount, response.body)
        verify(getAccountUseCase).getAccount(userId)
    }

    @Test
    fun shouldGetBalanceSuccessfully() {
        // GIVEN
        val mockBalanceResponse = AccountBalanceResponse(
            balance = 5000.0,
            availableBalance = 4800.0,
            currency = "USD",
            overdraftLimit = 200.0
        )

        whenever(getBalanceAccountUseCase.getBalance(userId)).thenReturn(mockBalanceResponse)

        // WHEN
        val response = accountController.getBalance(userId)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockBalanceResponse.balance, response.body?.balance)
        assertEquals(mockBalanceResponse.availableBalance, response.body?.availableBalance)
        assertEquals(mockBalanceResponse.currency, response.body?.currency)
        assertEquals(mockBalanceResponse.overdraftLimit, response.body?.overdraftLimit)

        verify(getBalanceAccountUseCase).getBalance(userId)
    }

    @Test
    fun shouldCreateAccountSuccessfully() {
        // GIVEN
        val requestDTO = AccountRequestDTO(
            accountType = "SAVINGS",
            currency = "USD",
            alias = "Nueva Cuenta",
            overdraftLimit = 0.0,
            availableBalance = 0.0,
            balance = 0.0,
            status = "ACTIVE"
        )
        whenever(request.requestURL).thenReturn(StringBuffer(mockUrl))
        whenever(dateFormatter.getDate()).thenReturn(mockDate)

        // WHEN
        val response = accountController.createAccount(userId, requestDTO, request)

        // THEN
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals("Account created successfully", response.body?.message)
        verify(createAccountUseCase).createAccount(userId, requestDTO)
    }

    @Test
    fun shouldUpdateAccountSuccessfully() {
        // GIVEN
        val requestDTO = AccountRequestDTO(
            accountType = "CHECKING",
            currency = "USD",
            alias = "Update",
            overdraftLimit = 0.0,
            availableBalance = 0.0,
            balance = 0.0,
            status = "ACTIVE"
        )
        whenever(request.requestURL).thenReturn(StringBuffer(mockUrl))
        whenever(dateFormatter.getDate()).thenReturn(mockDate)

        // WHEN
        val response = accountController.updateAccount(userId, requestDTO, request)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Account updated successfully", response.body?.message)
        verify(updateAccountUseCase).updateAccount(userId, requestDTO)
    }

    @Test
    fun shouldDeleteAccountSuccessfully() {
        // GIVEN
        whenever(request.requestURL).thenReturn(StringBuffer(mockUrl))
        whenever(dateFormatter.getDate()).thenReturn(mockDate)

        // WHEN
        val response = accountController.deleteAccount(userId, request)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Account deleted successfully", response.body?.message)
        verify(deleteAccountUseCase).deleteAccount(userId)
    }

    @Test
    fun shouldBlockAccountSuccessfully() {
        // GIVEN
        whenever(request.requestURL).thenReturn(StringBuffer(mockUrl))
        whenever(dateFormatter.getDate()).thenReturn(mockDate)

        // WHEN
        val response = accountController.blockAccount(userId, request)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Account blocked successfully", response.body?.message)
        verify(blockAccountUseCase).blockAccount(userId)
    }

    @Test
    fun shouldUnblockAccountSuccessfully() {
        // GIVEN
        whenever(request.requestURL).thenReturn(StringBuffer(mockUrl))
        whenever(dateFormatter.getDate()).thenReturn(mockDate)

        // WHEN
        val response = accountController.unblockAccount(userId, request)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Account unblocked successfully", response.body?.message)
        verify(unblockAccountUseCase).unBlockAccount(userId)
    }
}