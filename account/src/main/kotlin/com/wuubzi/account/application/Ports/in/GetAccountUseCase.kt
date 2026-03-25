package com.wuubzi.account.application.Ports.`in`

import com.wuubzi.account.domain.models.AccountModel
import java.util.UUID

interface GetAccountUseCase {
    fun getAccount(userId: UUID): AccountModel
}