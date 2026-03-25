package com.wuubzi.account.application.Ports.out

import com.wuubzi.account.domain.models.UserCachedModel

import java.util.UUID

interface UserCacheRepositoryPort {
    fun findByUserId(userId: UUID): UserCachedModel?
}