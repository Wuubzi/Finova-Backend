package com.wuubzi.auth.infrastructure.Persistence.Mappers


import com.wuubzi.auth.domain.models.UserCredentials
import com.wuubzi.auth.infrastructure.Persistence.DTOS.Request.UserRequest
import com.wuubzi.auth.infrastructure.Persistence.Entities.UserCredentialsEntity


fun UserCredentials.toEntity(): UserCredentialsEntity {
    return UserCredentialsEntity().apply {
        userId = this@toEntity.userId
        email = this@toEntity.email
        password = this@toEntity.password
        role = this@toEntity.role
        isActive = this@toEntity.isActive
        createdAt = this@toEntity.createdAt
    }
}

fun UserCredentialsEntity.toDomain(): UserCredentials =
    UserCredentials(
        id = id,
        userId = userId,
        email = email,
        password = password,
        role = role,
        isActive = isActive == true,
        createdAt = createdAt,
    )

fun UserRequest.toDomain(): UserCredentials =
    UserCredentials(
        id = null,
        email = email,
        password = password,
        userId = null,
        role = null,
        isActive = true,
        createdAt = null,
    )