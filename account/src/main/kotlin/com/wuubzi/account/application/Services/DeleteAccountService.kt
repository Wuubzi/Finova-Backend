package com.wuubzi.account.application.Services

import com.wuubzi.account.application.Ports.`in`.DeleteAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeleteAccountService(
    private val accountRepository: AccountRepositoryPort
): DeleteAccountUseCase {
    override fun deleteAccount(userId: UUID) {
        accountRepository.findByUserId(userId) ?: throw IllegalArgumentException("Account with user id $userId not found")
        accountRepository.deleteByUserId(userId)
    }
}