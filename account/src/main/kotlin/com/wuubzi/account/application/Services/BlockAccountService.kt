package com.wuubzi.account.application.Services

import com.wuubzi.account.application.Ports.`in`.BlockAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BlockAccountService(
    private val accountRepository: AccountRepositoryPort
): BlockAccountUseCase {
    override fun blockAccount(userId: UUID) {
        val accountExists = accountRepository.findByUserId(userId) ?: throw IllegalArgumentException("User with id $userId not found")
       if (accountExists.status == "BLOCKED") throw IllegalArgumentException("User with id $userId already blocked")
        val newAccount = accountExists.copy(
            status = "BLOCKED"
        )
        accountRepository.save(newAccount)
    }

}