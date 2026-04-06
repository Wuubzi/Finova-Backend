package com.wuubzi.account.infrastructure.Repositories

import com.wuubzi.account.infrastructure.Persistence.Entities.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountRepository: JpaRepository<AccountEntity, UUID> {
  fun findByUserId(userId: UUID): AccountEntity?
  fun findByAccountNumber(accountNumber: String): AccountEntity?
  fun deleteByUserId(userId: UUID)
}