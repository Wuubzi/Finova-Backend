package com.wuubzi.account.application.Ports.`in`

import com.wuubzi.account.application.DTOS.Response.AccountBalanceResponse
import java.util.UUID


interface GetBalanceAccountUseCase {
    fun getBalance(userId: UUID): AccountBalanceResponse
}