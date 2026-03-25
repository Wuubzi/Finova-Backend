package com.wuubzi.account.application.Services

import com.wuubzi.account.application.DTOS.Request.AccountRequestDTO
import com.wuubzi.account.application.Ports.`in`.CreateAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.application.Ports.out.UserCacheRepositoryPort
import com.wuubzi.account.domain.models.AccountModel
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

@Service
class CreateAccountService(
    private val accountRepository: AccountRepositoryPort,
    private val userCacheRepository: UserCacheRepositoryPort
): CreateAccountUseCase {
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
        accountRepository.save(accountSaved)
    }

    private fun generateAccountNumber(): String {
        return (1..20).map { (0..9).random() }.joinToString("")
    }
}