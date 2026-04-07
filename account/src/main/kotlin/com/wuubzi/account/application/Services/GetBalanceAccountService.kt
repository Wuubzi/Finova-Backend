package com.wuubzi.account.application.Services

import com.wuubzi.account.application.DTOS.Response.AccountBalanceResponse
import com.wuubzi.account.application.Ports.`in`.GetBalanceAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.application.Ports.out.CachePort
import com.wuubzi.account.domain.models.AccountModel
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class GetBalanceAccountService(
    private val accountRepository: AccountRepositoryPort,
    private val cachePort: CachePort
): GetBalanceAccountUseCase {

    companion object {
        private const val CACHE_PREFIX = "account:user:"
        private val CACHE_TTL = Duration.ofMinutes(15)
    }

    override fun getBalance(userId: UUID): AccountBalanceResponse {
        val cacheKey = "$CACHE_PREFIX$userId"
        val cached = cachePort.getObject(cacheKey, AccountModel::class.java)
        val account = cached ?: (accountRepository.findByUserId(userId)
            ?: throw IllegalArgumentException("User with id $userId not found")).also {
            cachePort.saveObject(cacheKey, it, CACHE_TTL)
        }

        return AccountBalanceResponse(
            balance = account.balance,
            availableBalance = account.availableBalance,
            currency = account.currency,
            overdraftLimit = account.overdraftLimit
        )
    }

}