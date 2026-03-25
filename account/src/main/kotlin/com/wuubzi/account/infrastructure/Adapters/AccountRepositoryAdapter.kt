package com.wuubzi.account.infrastructure.Adapters


import com.wuubzi.account.application.Ports.out.AccountRepositoryPort
import com.wuubzi.account.domain.models.AccountModel
import com.wuubzi.account.infrastructure.Persistence.Mappers.toDomain
import com.wuubzi.account.infrastructure.Persistence.Mappers.toEntity
import com.wuubzi.account.infrastructure.Repositories.AccountRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class AccountRepositoryAdapter(
    private val accountRepository: AccountRepository
): AccountRepositoryPort {
    override fun save(account: AccountModel)  = accountRepository.save(account.toEntity()).toDomain()
    override fun findByUserId(userId: UUID): AccountModel? = accountRepository.findByUserId(userId)?.toDomain()
    @Transactional
    override fun deleteByUserId(userId: UUID) = accountRepository.deleteByUserId(userId)

}