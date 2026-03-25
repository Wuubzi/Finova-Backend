package com.wuubzi.account.application.Ports.`in`

import java.util.UUID

interface BlockAccountUseCase {
    fun blockAccount(userId: UUID)
}