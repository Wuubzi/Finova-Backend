package com.wuubzi.account.application.Ports.out


import com.wuubzi.account.domain.models.AccountModel
import java.util.UUID

interface AccountRepositoryPort {
    fun save(account: AccountModel): AccountModel
    fun findByUserId(userId: UUID): AccountModel?
    fun deleteByUserId(userId: UUID)
}