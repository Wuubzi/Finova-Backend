package com.wuubzi.auth.application.Ports.`in`

import com.wuubzi.auth.domain.models.UserCredentials

interface CreateUserUseCase {
    fun createUser(userCredentials: UserCredentials): UserCredentials
}