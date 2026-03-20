package com.wuubzi.user.application.Ports.In


import com.wuubzi.user.domain.Models.User
import java.util.UUID

interface GetUserUseCase {
    fun getUser(userId: UUID): User
}