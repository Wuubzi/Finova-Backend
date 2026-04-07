package com.wuubzi.auth.application.Ports.out

import com.wuubzi.auth.domain.models.UserCredentials
import java.util.UUID

interface UserCredentialsRepositoryPort {
    fun save(userCredentials: UserCredentials): UserCredentials
    fun findByEmail(email: String?): UserCredentials?
    fun findByUserId(userId: UUID): UserCredentials?
}