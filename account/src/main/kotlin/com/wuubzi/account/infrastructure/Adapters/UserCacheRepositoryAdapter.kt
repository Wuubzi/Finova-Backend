package com.wuubzi.account.infrastructure.Adapters

import com.wuubzi.account.application.Ports.out.UserCacheRepositoryPort
import com.wuubzi.account.infrastructure.Persistence.Mappers.toDomain
import com.wuubzi.account.infrastructure.Repositories.UserCacheRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserCacheRepositoryAdapter(
    private val userCacheRepository: UserCacheRepository
): UserCacheRepositoryPort {
    override fun findByUserId(userId: UUID) = userCacheRepository.findByUserId(userId)?.toDomain()
}