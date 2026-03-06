package com.wuubzi.auth.infrastructure.Repositories

import com.wuubzi.auth.infrastructure.Persistence.Entities.UserCredentialsEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserCredentialsRepository: JpaRepository<UserCredentialsEntity, Long> {
    fun save(userCredentials: UserCredentialsEntity): UserCredentialsEntity
    fun findByEmail(email: String): UserCredentialsEntity?
}