package com.wuubzi.auth.application.Ports.out

import com.wuubzi.auth.domain.models.UserCredentials

interface UserCredentialsRepositoryPort {
    fun save(userCredentials: UserCredentials): UserCredentials
    fun findByEmail(email: String?): UserCredentials?
}