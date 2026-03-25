package com.wuubzi.account.application.Ports.`in`

import java.util.UUID

interface UnBlockAccountUseCase {
    fun unBlockAccount(userId: UUID)
}