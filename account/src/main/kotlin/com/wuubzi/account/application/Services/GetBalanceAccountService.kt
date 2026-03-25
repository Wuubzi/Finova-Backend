package com.wuubzi.account.application.Services

import com.wuubzi.account.application.DTOS.Response.AccountBalanceResponse
import com.wuubzi.account.application.Ports.`in`.GetBalanceAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetBalanceAccountService(
    private val accountRepository: AccountRepositoryPort
): GetBalanceAccountUseCase {
    override fun getBalance(userId: UUID): AccountBalanceResponse {
        val account = accountRepository.findByUserId(userId) ?: throw IllegalArgumentException("User with id $userId not found")
        return AccountBalanceResponse(
            balance = account.balance,
            availableBalance = account.availableBalance,
            currency = account.currency,
            overdraftLimit = account.overdraftLimit
        )
    }


}