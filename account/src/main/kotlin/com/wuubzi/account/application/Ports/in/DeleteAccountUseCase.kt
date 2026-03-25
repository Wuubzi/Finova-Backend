package com.wuubzi.account.application.Ports.`in`

import java.util.UUID

fun interface DeleteAccountUseCase {
    fun deleteAccount(userId: UUID)
}