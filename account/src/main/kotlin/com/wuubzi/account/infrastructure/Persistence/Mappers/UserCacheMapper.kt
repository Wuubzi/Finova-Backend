package com.wuubzi.account.infrastructure.Persistence.Mappers

import com.wuubzi.account.domain.models.UserCachedModel
import com.wuubzi.account.infrastructure.Persistence.Entities.UserCacheEntity

fun UserCachedModel.toEntity(): UserCacheEntity {
    return UserCacheEntity().apply {
        userId = this@toEntity.userId
    }
}

fun UserCacheEntity.toDomain(): UserCachedModel {
    return UserCachedModel(
        userId = userId!!
    )
}