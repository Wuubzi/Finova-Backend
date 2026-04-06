package com.wuubzi.account.application.Services

import com.wuubzi.account.application.Ports.`in`.BlockAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.application.Ports.out.CachePort
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class BlockAccountService(
    private val accountRepository: AccountRepositoryPort,
    private val cachePort: CachePort
): BlockAccountUseCase {

    companion object {
        private const val CACHE_PREFIX_USER = "account:user:"
        private const val CACHE_PREFIX_NUMBER = "account:number:"
        private val CACHE_TTL = Duration.ofMinutes(15)
    }

    override fun blockAccount(userId: UUID) {
        val accountExists = accountRepository.findByUserId(userId) ?: throw IllegalArgumentException("User with id $userId not found")
        require(accountExists.status != "BLOCKED") { "User with id $userId already blocked" }
        val newAccount = accountExists.copy(
            status = "BLOCKED"
        )
        val saved = accountRepository.save(newAccount)

        cachePort.saveObject("$CACHE_PREFIX_USER$userId", saved, CACHE_TTL)
        cachePort.saveObject("$CACHE_PREFIX_NUMBER${saved.accountNumber}", saved, CACHE_TTL)
    }
}