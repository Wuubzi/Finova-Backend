package com.wuubzi.auth.application.Ports.`in`

import com.wuubzi.auth.application.DTOS.Request.UserRequest
import com.wuubzi.auth.domain.models.UserCredentials

interface CreateUserUseCase {
    fun createUser(userRequest: UserRequest): UserCredentials
}