package com.wuubzi.account.application.Services

import com.wuubzi.account.application.DTOS.Request.AccountRequestDTO
import com.wuubzi.account.application.Ports.`in`.UpdateAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.application.Ports.out.CachePort
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class UpdateAccountService(
    private val accountRepository: AccountRepositoryPort,
    private val cachePort: CachePort
): UpdateAccountUseCase {

    companion object {
        private const val CACHE_PREFIX_USER = "account:user:"
        private const val CACHE_PREFIX_NUMBER = "account:number:"
        private val CACHE_TTL = Duration.ofMinutes(15)
    }

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

        val saved = accountRepository.save(newAccount)

        cachePort.saveObject("$CACHE_PREFIX_USER$userId", saved, CACHE_TTL)
        cachePort.saveObject("$CACHE_PREFIX_NUMBER${saved.accountNumber}", saved, CACHE_TTL)
    }
}