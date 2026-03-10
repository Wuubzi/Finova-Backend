package com.wuubzi.auth.infrastructure.Repositories

import com.wuubzi.auth.infrastructure.Persistence.Entities.UserCredentialsEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserCredentialsRepository: JpaRepository<UserCredentialsEntity, UUID> {
    fun save(userCredentials: UserCredentialsEntity): UserCredentialsEntity
    fun findByEmail(email: String?): UserCredentialsEntity?
}