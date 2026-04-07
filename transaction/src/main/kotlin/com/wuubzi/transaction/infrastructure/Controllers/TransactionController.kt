package com.wuubzi.transaction.infrastructure.Controllers

import com.wuubzi.transaction.Utils.DateFormatter
import com.wuubzi.transaction.application.DTOS.Request.DepositRequest
import com.wuubzi.transaction.application.DTOS.Request.TransferRequest
import com.wuubzi.transaction.application.DTOS.Request.WithdrawRequest
import com.wuubzi.transaction.application.DTOS.Response.Response
import com.wuubzi.transaction.application.Ports.In.DepositUseCase
import com.wuubzi.transaction.application.Ports.In.GetAllTransactionUseCase
import com.wuubzi.transaction.application.Ports.In.GetTransactionUseCase
import com.wuubzi.transaction.application.Ports.In.TransferUseCase
import com.wuubzi.transaction.application.Ports.In.WithdrawUseCase
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("api/v1/transaction")
class TransactionController(
    private val depositUseCase: DepositUseCase,
    private val withdrawUseCase: WithdrawUseCase,
    private val transferUseCase: TransferUseCase,
    private val getTransactionUseCase: GetTransactionUseCase,
    private val getAllTransaction: GetAllTransactionUseCase,
    private val dateFormatter: DateFormatter
) {

    private fun getEmailFromJwt(): String {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No authentication found")
        val jwt = authentication.principal as Jwt
        return jwt.getClaimAsString("email")
            ?: throw IllegalStateException("Email claim not found in JWT")
    }

    @GetMapping("/all")
    fun getAllTransactions(@RequestParam("accountNumber") accountNumber: String) = getAllTransaction.getAllTransactions(accountNumber)

    @GetMapping("/{transactionId}")
    fun getTransactionById(@PathVariable transactionId: UUID) = getTransactionUseCase.getTransaction(transactionId)

    @PostMapping("/deposit")
    fun deposit(@RequestBody @Valid body: DepositRequest, request: HttpServletRequest): ResponseEntity<Response> {
        depositUseCase.deposit(body.accountNumber, body.amount, getEmailFromJwt())
        val responseBody = Response(
            message = "Deposit successful",
            url = request.requestURL.toString(),
            code = 200,
            timestamp = dateFormatter.getDate()
        )
        return ResponseEntity(responseBody, HttpStatus.OK)
    }

    @PostMapping("/withdraw")
    fun withdraw(@RequestBody @Valid body: WithdrawRequest, request: HttpServletRequest): ResponseEntity<Response> {
        withdrawUseCase.withdraw(body.accountNumber, body.amount, getEmailFromJwt())
        val responseBody = Response(
            message = "Withdrawal successful",
            url = request.requestURL.toString(),
            code = 200,
            timestamp = dateFormatter.getDate()
        )
        return ResponseEntity(responseBody, HttpStatus.OK)
    }

    @PostMapping("/transfer")
    fun transfer(@RequestBody @Valid body: TransferRequest, request: HttpServletRequest): ResponseEntity<Response> {
        transferUseCase.transfer(body.fromAccountNumber, body.toAccountNumber, body.amount, getEmailFromJwt())
        val responseBody = Response(
            message = "Transfer successful",
            url = request.requestURL.toString(),
            code = 200,
            timestamp = dateFormatter.getDate()
        )
        return ResponseEntity(responseBody, HttpStatus.OK)
    }

}