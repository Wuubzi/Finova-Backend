package com.wuubzi.transaction.application.Ports.In

fun interface DepositUseCase {
    fun deposit(accountNumber: String, amount: Double, email: String)
}