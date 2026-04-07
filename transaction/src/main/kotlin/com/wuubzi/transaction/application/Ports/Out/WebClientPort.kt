package com.wuubzi.transaction.application.Ports.Out

import com.wuubzi.transaction.application.DTOS.Response.AccountResponse
import java.util.UUID

interface WebClientPort {
    fun getAccountId(accountId: UUID ): AccountResponse?
    fun getAccountByAccountNumber(accountNumber: String): AccountResponse?
}