package com.wuubzi.account.application.Services

import com.wuubzi.account.application.Ports.`in`.GetAccountUseCase
import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.domain.models.AccountModel
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetAccountService(
    private val accountRepository: AccountRepositoryPort
): GetAccountUseCase {
    override fun getAccount(userId: UUID): AccountModel  =  accountRepository.findByUserId(userId) ?: throw IllegalArgumentException("This user id $userId dont have any account")
}