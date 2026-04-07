package com.wuubzi.transaction.application.Ports.In

import com.wuubzi.transaction.domain.Models.TransactionModel

fun interface GetAllTransactionUseCase {
    fun getAllTransactions(accountNumber: String): List<TransactionModel>
}