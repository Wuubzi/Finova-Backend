package com.wuubzi.account.application.Services

import com.wuubzi.account.application.Ports.`in`.UnBlockAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.application.Ports.out.CachePort
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class UnBlockAccountService(
    private val accountRepository: AccountRepositoryPort,
    private val cachePort: CachePort
): UnBlockAccountUseCase {

    companion object {
        private const val CACHE_PREFIX_USER = "account:user:"
        private const val CACHE_PREFIX_NUMBER = "account:number:"
        private val CACHE_TTL = Duration.ofMinutes(15)
    }

    override fun unBlockAccount(userId: UUID) {
        val accountExists = accountRepository.findByUserId(userId) ?: throw IllegalArgumentException("User with id $userId not found")
        require(accountExists.status != "ACTIVE") { "User with id $userId already active" }
        val newAccount = accountExists.copy(
            status = "ACTIVE"
        )
        val saved = accountRepository.save(newAccount)

        cachePort.saveObject("$CACHE_PREFIX_USER$userId", saved, CACHE_TTL)
        cachePort.saveObject("$CACHE_PREFIX_NUMBER${saved.accountNumber}", saved, CACHE_TTL)
    }
}