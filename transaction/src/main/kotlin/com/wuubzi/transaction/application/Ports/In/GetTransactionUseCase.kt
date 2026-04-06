package com.wuubzi.transaction.application.Ports.In

import com.wuubzi.transaction.domain.Models.TransactionModel
import java.util.UUID

fun interface GetTransactionUseCase {
    fun getTransaction(transactionId: UUID): TransactionModel
}