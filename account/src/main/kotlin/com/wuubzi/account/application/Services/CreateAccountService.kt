package com.wuubzi.account.application.Services

import com.wuubzi.account.application.DTOS.Request.AccountRequestDTO
import com.wuubzi.account.application.Ports.`in`.CreateAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.application.Ports.out.CachePort
import com.wuubzi.account.application.Ports.out.UserCacheRepositoryPort
import com.wuubzi.account.domain.models.AccountModel
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class CreateAccountService(
    private val accountRepository: AccountRepositoryPort,
    private val userCacheRepository: UserCacheRepositoryPort,
    private val cachePort: CachePort
): CreateAccountUseCase {

    companion object {
        private const val CACHE_PREFIX_USER = "account:user:"
        private const val CACHE_PREFIX_NUMBER = "account:number:"
        private val CACHE_TTL = Duration.ofMinutes(15)
    }

    override fun createAccount(userId: UUID, account: AccountRequestDTO) {
        userCacheRepository.findByUserId(userId) ?: throw IllegalArgumentException("User with id $userId not found")
        accountRepository.findByUserId(userId)?.let {
            throw IllegalArgumentException("User with id $userId already has an account")
        }

        val now = Timestamp.from(Instant.now())

        val accountSaved = AccountModel(
            accountId = UUID.randomUUID(),
            accountNumber = generateAccountNumber(),
            userId = userId,
            accountType = account.accountType,
            currency = account.currency,
            balance = account.balance,
            availableBalance = account.availableBalance,
            status = account.status,
            alias = account.alias,
            overdraftLimit = account.overdraftLimit,
            createdAt = now,
            updatedAt = now
        )
        val saved = accountRepository.save(accountSaved)

        cachePort.saveObject("$CACHE_PREFIX_USER$userId", saved, CACHE_TTL)
        cachePort.saveObject("$CACHE_PREFIX_NUMBER${saved.accountNumber}", saved, CACHE_TTL)
    }

    private fun generateAccountNumber(): String {
        return (1..20).map { (0..9).random() }.joinToString("")
    }
}