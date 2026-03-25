package com.wuubzi.account.application.Services

import com.wuubzi.account.application.Ports.`in`.UnBlockAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UnBlockAccountService(
    private val accountRepository: AccountRepositoryPort
): UnBlockAccountUseCase {
    override fun unBlockAccount(userId: UUID) {
        val accountExists = accountRepository.findByUserId(userId) ?: throw IllegalArgumentException("User with id $userId not found")
        if (accountExists.status == "ACTIVE") throw IllegalArgumentException("User with id $userId already active")
        val newAccount = accountExists.copy(
            status = "ACTIVE"
        )
        accountRepository.save(newAccount)
    }
}