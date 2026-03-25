package com.wuubzi.account.application.Ports.`in`

import java.util.UUID

interface DeleteAccountUseCase {
    fun deleteAccount(userId: UUID)
}