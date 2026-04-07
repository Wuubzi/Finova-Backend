package com.wuubzi.account.application.Ports.`in`

import com.wuubzi.account.domain.models.AccountModel

fun interface GetAccountByAccountNumberUseCase {
    fun getAccountByAccountNumber(accountNumber: String): AccountModel
}

