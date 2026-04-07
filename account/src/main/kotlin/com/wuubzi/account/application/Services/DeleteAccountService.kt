package com.wuubzi.account.application.Services

import com.wuubzi.account.application.Ports.`in`.DeleteAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.application.Ports.out.CachePort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeleteAccountService(
    private val accountRepository: AccountRepositoryPort,
    private val cachePort: CachePort
): DeleteAccountUseCase {

    companion object {
        private const val CACHE_PREFIX_USER = "account:user:"
        private const val CACHE_PREFIX_NUMBER = "account:number:"
    }

    override fun deleteAccount(userId: UUID) {
        val account = accountRepository.findByUserId(userId) ?: throw IllegalArgumentException("Account with user id $userId not found")
        accountRepository.deleteByUserId(userId)

        cachePort.delete("$CACHE_PREFIX_USER$userId")
        cachePort.delete("$CACHE_PREFIX_NUMBER${account.accountNumber}")
    }
}