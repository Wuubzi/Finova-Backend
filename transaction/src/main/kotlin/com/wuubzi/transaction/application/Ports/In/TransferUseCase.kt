package com.wuubzi.transaction.application.Ports.In

fun interface TransferUseCase {
    fun transfer(fromAccountNumber: String, toAccountNumber: String, amount: Double, email: String)
}