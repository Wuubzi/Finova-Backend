package com.wuubzi.transaction.Controllers

import com.wuubzi.transaction.Utils.DateFormatter
import com.wuubzi.transaction.application.DTOS.Request.DepositRequest
import com.wuubzi.transaction.application.DTOS.Request.TransferRequest
import com.wuubzi.transaction.application.DTOS.Request.WithdrawRequest
import com.wuubzi.transaction.application.Ports.In.DepositUseCase
import com.wuubzi.transaction.application.Ports.In.GetAllTransactionUseCase
import com.wuubzi.transaction.application.Ports.In.GetTransactionUseCase
import com.wuubzi.transaction.application.Ports.In.TransferUseCase
import com.wuubzi.transaction.application.Ports.In.WithdrawUseCase
import com.wuubzi.transaction.domain.Models.TransactionModel
import com.wuubzi.transaction.infrastructure.Controllers.TransactionController
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class TransactionControllerTest {

    @Mock
    lateinit var depositUseCase: DepositUseCase

    @Mock
    lateinit var withdrawUseCase: WithdrawUseCase

    @Mock
    lateinit var transferUseCase: TransferUseCase

    @Mock
    lateinit var getTransactionUseCase: GetTransactionUseCase

    @Mock
    lateinit var getAllTransaction: GetAllTransactionUseCase

    @Mock
    lateinit var dateFormatter: DateFormatter

    @Mock
    lateinit var request: HttpServletRequest

    @Mock
    lateinit var securityContext: SecurityContext

    @Mock
    lateinit var authentication: Authentication

    @Mock
    lateinit var jwt: Jwt

    @InjectMocks
    lateinit var controller: TransactionController

    private val mockDate = "2026-04-06"
    private val mockUrl = "http://localhost/api/v1/transaction"
    private val email = "test@email.com"
    private val transactionId = UUID.randomUUID()
    private val accountNumber = "12345678901234567890"
    private val now = Timestamp.from(Instant.now())

    @BeforeEach
    fun setup() {
        lenient().whenever(securityContext.authentication).thenReturn(authentication)
        lenient().whenever(authentication.principal).thenReturn(jwt)
        lenient().whenever(jwt.getClaimAsString("email")).thenReturn(email)
        SecurityContextHolder.setContext(securityContext)
    }

    @AfterEach
    fun cleanup() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun shouldDepositSuccessfully() {
        // GIVEN
        val body = DepositRequest(accountNumber = accountNumber, amount = 500.0)
        whenever(request.requestURL).thenReturn(StringBuffer(mockUrl))
        whenever(dateFormatter.getDate()).thenReturn(mockDate)

        // WHEN
        val response = controller.deposit(body, request)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Deposit successful", response.body?.message)
        assertEquals(200, response.body?.code)
        verify(depositUseCase).deposit(accountNumber, 500.0, email)
    }

    @Test
    fun shouldWithdrawSuccessfully() {
        // GIVEN
        val body = WithdrawRequest(accountNumber = accountNumber, amount = 200.0)
        whenever(request.requestURL).thenReturn(StringBuffer(mockUrl))
        whenever(dateFormatter.getDate()).thenReturn(mockDate)

        // WHEN
        val response = controller.withdraw(body, request)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Withdrawal successful", response.body?.message)
        verify(withdrawUseCase).withdraw(accountNumber, 200.0, email)
    }

    @Test
    fun shouldTransferSuccessfully() {
        // GIVEN
        val toAccountNumber = "99999999999999999999"
        val body = TransferRequest(
            fromAccountNumber = accountNumber,
            toAccountNumber = toAccountNumber,
            amount = 300.0
        )
        whenever(request.requestURL).thenReturn(StringBuffer(mockUrl))
        whenever(dateFormatter.getDate()).thenReturn(mockDate)

        // WHEN
        val response = controller.transfer(body, request)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Transfer successful", response.body?.message)
        verify(transferUseCase).transfer(accountNumber, toAccountNumber, 300.0, email)
    }

    @Test
    fun shouldGetTransactionById() {
        // GIVEN
        val transaction = TransactionModel(
            transactionId = transactionId,
            fromAccountId = UUID.randomUUID(),
            toAccountId = UUID.randomUUID(),
            amount = 100.0,
            currency = "USD",
            status = "COMPLETED",
            type = "DEPOSIT",
            description = "Test",
            createdAt = now
        )
        whenever(getTransactionUseCase.getTransaction(transactionId)).thenReturn(transaction)

        // WHEN
        val result = controller.getTransactionById(transactionId)

        // THEN
        assertEquals(transaction, result)
        verify(getTransactionUseCase).getTransaction(transactionId)
    }

    @Test
    fun shouldGetAllTransactions() {
        // GIVEN
        val transactions = listOf(
            TransactionModel(
                transactionId = UUID.randomUUID(),
                fromAccountId = null,
                toAccountId = UUID.randomUUID(),
                amount = 50.0,
                currency = "USD",
                status = "COMPLETED",
                type = "DEPOSIT",
                description = "Deposit",
                createdAt = now
            )
        )
        whenever(getAllTransaction.getAllTransactions(accountNumber)).thenReturn(transactions)

        // WHEN
        val result = controller.getAllTransactions(accountNumber)

        // THEN
        assertEquals(transactions, result)
        verify(getAllTransaction).getAllTransactions(accountNumber)
    }
}

