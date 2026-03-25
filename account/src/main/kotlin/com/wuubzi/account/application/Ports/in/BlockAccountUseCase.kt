package com.wuubzi.account.application.Ports.`in`

import java.util.UUID

fun interface BlockAccountUseCase {
    fun blockAccount(userId: UUID)
}