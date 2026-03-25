package com.wuubzi.account.application.Ports.`in`

import com.wuubzi.account.application.DTOS.Response.AccountBalanceResponse
import java.util.UUID


fun interface GetBalanceAccountUseCase {
    fun getBalance(userId: UUID): AccountBalanceResponse
}