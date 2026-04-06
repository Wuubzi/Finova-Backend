package com.wuubzi.transaction.application.Ports.In

fun interface WithdrawUseCase {
    fun withdraw(accountNumber: String, amount: Double, email: String)
}