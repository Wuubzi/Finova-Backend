package com.wuubzi.account.application.Services

import com.wuubzi.account.application.DTOS.Request.AccountRequestDTO
import com.wuubzi.account.application.Ports.`in`.UpdateAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UpdateAccountService(
    private val accountRepository: AccountRepositoryPort
): UpdateAccountUseCase {
    override fun updateAccount(
        userId: UUID,
        account: AccountRequestDTO
    ) {
        val accountExist = accountRepository.findByUserId(userId) ?: throw IllegalArgumentException("Account with user id $userId not found")

        val newAccount = accountExist.copy(
            accountType = account.accountType,
            currency = account.currency,
            overdraftLimit = account.overdraftLimit,
            balance = account.balance,
            availableBalance = account.availableBalance,
            status = account.status,
            alias = account.alias,
        )

        accountRepository.save(newAccount)
    }

}