package com.wuubzi.account.application.Ports.`in`

import com.wuubzi.account.application.DTOS.Request.AccountRequestDTO
import java.util.UUID

interface UpdateAccountUseCase {
    fun updateAccount(userId: UUID, account: AccountRequestDTO)
}