package com.wuubzi.account.application.Ports.`in`

import com.wuubzi.account.application.DTOS.Request.AccountRequestDTO
import java.util.UUID

fun interface CreateAccountUseCase {
    fun createAccount(userId: UUID, account: AccountRequestDTO)
}