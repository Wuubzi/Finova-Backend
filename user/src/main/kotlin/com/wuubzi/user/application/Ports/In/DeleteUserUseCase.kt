package com.wuubzi.user.application.Ports.In

import java.util.UUID

interface DeleteUserUseCase {
    fun deleteUser(userId: UUID)
}