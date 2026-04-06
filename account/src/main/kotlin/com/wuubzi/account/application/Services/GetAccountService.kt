package com.wuubzi.account.application.Services

import com.wuubzi.account.application.Ports.`in`.GetAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.application.Ports.out.CachePort
import com.wuubzi.account.domain.models.AccountModel
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class GetAccountService(
    private val accountRepository: AccountRepositoryPort,
    private val cachePort: CachePort
): GetAccountUseCase {

    companion object {
        private const val CACHE_PREFIX = "account:user:"
        private val CACHE_TTL = Duration.ofMinutes(15)
    }

    override fun getAccount(userId: UUID): AccountModel {
        val cacheKey = "$CACHE_PREFIX$userId"
        val cached = cachePort.getObject(cacheKey, AccountModel::class.java)
        if (cached != null) return cached

        val account = accountRepository.findByUserId(userId)
            ?: throw IllegalArgumentException("This user id $userId dont have any account")

        cachePort.saveObject(cacheKey, account, CACHE_TTL)
        return account
    }
}