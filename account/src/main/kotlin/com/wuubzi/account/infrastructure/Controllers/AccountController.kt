package com.wuubzi.account.infrastructure.Controllers

import com.wuubzi.account.application.DTOS.Request.AccountRequestDTO
import com.wuubzi.account.application.DTOS.Response.AccountBalanceResponse
import com.wuubzi.account.application.DTOS.Response.Response
import com.wuubzi.account.application.Ports.`in`.BlockAccountUseCase
import com.wuubzi.account.application.Ports.`in`.CreateAccountUseCase
import com.wuubzi.account.application.Ports.`in`.DeleteAccountUseCase
import com.wuubzi.account.application.Ports.`in`.GetAccountUseCase
import com.wuubzi.account.application.Ports.`in`.GetBalanceAccountUseCase
import com.wuubzi.account.application.Ports.`in`.UnBlockAccountUseCase
import com.wuubzi.account.application.Ports.`in`.UpdateAccountUseCase
import com.wuubzi.account.domain.models.AccountModel
import com.wuubzi.account.utils.DateFormatter
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.apache.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("api/v1/account")
class AccountController(
    private val createAccountUseCase: CreateAccountUseCase,
    private val blockAccountUseCase: BlockAccountUseCase,
    private val unblockAccountUseCase: UnBlockAccountUseCase,
    private val getBalanceAccountUseCase: GetBalanceAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val getAccountUseCase: GetAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val dateFormatter: DateFormatter,
) {

    @GetMapping
    fun getAccount(@RequestHeader("X-User-Id") userId: UUID): ResponseEntity<AccountModel> {
        return ResponseEntity.status(HttpStatus.SC_OK).body(getAccountUseCase.getAccount(userId))
    }
    @PostMapping
    fun createAccount(
        @RequestHeader("X-User-Id") userId: UUID,
        @Valid @RequestBody account: AccountRequestDTO,
        request: HttpServletRequest
    ):ResponseEntity<Response> {
        createAccountUseCase.createAccount(userId, account)
        val responseBody = Response(
            message = "Account created successfully",
            url = request.requestURL.toString(),
            code = HttpStatus.SC_CREATED,
            timestamp = dateFormatter.getDate()
        )
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(responseBody)
    }
    @PutMapping
    fun updateAccount(
        @RequestHeader("X-User-Id") userId: UUID,
        @Valid @RequestBody account: AccountRequestDTO,
        request: HttpServletRequest
    ):ResponseEntity<Response> {
        updateAccountUseCase.updateAccount(userId, account)
        val responseBody = Response(
            message = "Account updated successfully",
            url = request.requestURL.toString(),
            code = HttpStatus.SC_OK,
            timestamp = dateFormatter.getDate()
        )
        return ResponseEntity.status(HttpStatus.SC_OK).body(responseBody)

    }
    @DeleteMapping
    fun deleteAccount(
        @RequestHeader("X-User-Id") userId: UUID,
        request: HttpServletRequest
    ):ResponseEntity<Response> {
        deleteAccountUseCase.deleteAccount(userId)
        val responseBody = Response(
            message = "Account deleted successfully",
            url = request.requestURL.toString(),
            code = HttpStatus.SC_OK,
            timestamp = dateFormatter.getDate()
        )
        return ResponseEntity.status(HttpStatus.SC_OK).body(responseBody)
    }
    @GetMapping("/balance")
    fun getBalance(
        @RequestHeader("X-User-Id") userId: UUID,
    ): ResponseEntity<AccountBalanceResponse> {
        return ResponseEntity.status(HttpStatus.SC_OK).body(getBalanceAccountUseCase.getBalance(userId))

    }
    @PutMapping("/block")
    fun blockAccount(
        @RequestHeader("X-User-Id") userId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<Response> {
        blockAccountUseCase.blockAccount(userId)
        val responseBody = Response(
            message = "Account blocked successfully",
            url = request.requestURL.toString(),
            code = HttpStatus.SC_OK,
            timestamp = dateFormatter.getDate()
        )
        return ResponseEntity.status(HttpStatus.SC_OK).body(responseBody)

    }
    @PutMapping("/unblock")
    fun unblockAccount(
        @RequestHeader("X-User-Id") userId: UUID,
        request: HttpServletRequest
    ): ResponseEntity<Response> {
        unblockAccountUseCase.unBlockAccount(userId)
        val responseBody = Response(
            message = "Account unblocked successfully",
            url = request.requestURL.toString(),
            code = HttpStatus.SC_OK,
            timestamp = dateFormatter.getDate()
        )

        return ResponseEntity.status(HttpStatus.SC_OK).body(responseBody)
    }

}