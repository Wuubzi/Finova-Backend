package com.wuubzi.account.infrastructure.Repositories

import com.wuubzi.account.infrastructure.Persistence.Entities.UserCacheEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID


interface UserCacheRepository: JpaRepository<UserCacheEntity, UUID> {
    fun findByUserId(userId: UUID): UserCacheEntity?
}