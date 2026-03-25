package com.wuubzi.account.application.Ports.`in`

import java.util.UUID

fun interface UnBlockAccountUseCase {
    fun unBlockAccount(userId: UUID)
}