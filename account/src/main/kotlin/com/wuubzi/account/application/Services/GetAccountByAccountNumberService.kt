package com.wuubzi.account.application.Services

import com.wuubzi.account.application.Exceptions.AccountNotFoundException
import com.wuubzi.account.application.Ports.`in`.GetAccountByAccountNumberUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.application.Ports.out.CachePort
import com.wuubzi.account.domain.models.AccountModel
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class GetAccountByAccountNumberService(
    private val accountRepository: AccountRepositoryPort,
    private val cachePort: CachePort
) : GetAccountByAccountNumberUseCase {

    companion object {
        private const val CACHE_PREFIX = "account:number:"
        private val CACHE_TTL = Duration.ofMinutes(15)
    }

    override fun getAccountByAccountNumber(accountNumber: String): AccountModel {
        val cacheKey = "$CACHE_PREFIX$accountNumber"
        val cached = cachePort.getObject(cacheKey, AccountModel::class.java)
        if (cached != null) return cached

        val account = accountRepository.findByAccountNumber(accountNumber)
            ?: throw AccountNotFoundException("Account with number $accountNumber not found")

        cachePort.saveObject(cacheKey, account, CACHE_TTL)
        return account
    }
}

