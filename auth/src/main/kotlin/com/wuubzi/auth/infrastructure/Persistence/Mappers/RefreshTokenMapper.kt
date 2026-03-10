package com.wuubzi.auth.infrastructure.Persistence.Mappers

import com.wuubzi.auth.domain.models.RefreshToken
import com.wuubzi.auth.infrastructure.Persistence.Entities.RefreshTokenEntity

fun RefreshToken.toEntity(): RefreshTokenEntity {
    return RefreshTokenEntity().apply {
        id = this@toEntity.id
        userId = this@toEntity.userId
        token = this@toEntity.token
        expiresAt = this@toEntity.expiresAt
        isRevoked = this@toEntity.isRevoked
        createdAt = this@toEntity.createdAt
    }
}

fun RefreshTokenEntity.toDomain(): RefreshToken =
    RefreshToken(
        id = id!!,
        userId = userId!!,
        token = token!!,
        expiresAt = expiresAt!!,
        isRevoked =  isRevoked!!,
        createdAt =  createdAt
    )