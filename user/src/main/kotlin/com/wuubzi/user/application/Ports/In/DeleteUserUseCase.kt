package com.wuubzi.user.application.Ports.In

import java.util.UUID

fun interface DeleteUserUseCase {
    fun deleteUser(userId: UUID)
}