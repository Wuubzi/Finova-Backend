package com.wuubzi.auth.infrastructure.Persistence.Mappers


import com.wuubzi.auth.domain.models.UserCredentials
import com.wuubzi.auth.infrastructure.Persistence.Entities.UserCredentialsEntity


fun UserCredentials.toEntity(): UserCredentialsEntity {
    return UserCredentialsEntity().apply {
        id = this@toEntity.id
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
        id = id!!,
        userId = userId!!,
        email = email!!,
        password = password!!,
        role = role!!,
        isActive = isActive ?: false,
        createdAt = createdAt!!,
    )
