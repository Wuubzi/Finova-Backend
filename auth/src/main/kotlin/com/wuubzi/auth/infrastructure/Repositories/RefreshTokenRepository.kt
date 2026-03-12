package com.wuubzi.auth.infrastructure.Repositories

import com.wuubzi.auth.infrastructure.Persistence.Entities.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RefreshTokenRepository: JpaRepository<RefreshTokenEntity, UUID> {
    fun save(refreshToken: RefreshTokenEntity): RefreshTokenEntity
    fun findByToken(token: String): RefreshTokenEntity?
}