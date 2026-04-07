package com.wuubzi.transaction.application.Ports.Out

import com.wuubzi.transaction.domain.Models.TransactionModel
import java.util.UUID

interface TransactionRepositoryPort {
    fun save(transaction: TransactionModel): TransactionModel
    fun findById(transactionId: UUID): TransactionModel?
    fun findAllByAccountId(accountId: UUID): List<TransactionModel>
    fun updateStatus(transactionId: UUID, status: String)
}