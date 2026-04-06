package com.wuubzi.auth.infrastructure.Adapters

import com.wuubzi.auth.application.Ports.out.UserCredentialsRepositoryPort
import com.wuubzi.auth.domain.models.UserCredentials
import com.wuubzi.auth.infrastructure.Persistence.Mappers.toDomain
import com.wuubzi.auth.infrastructure.Persistence.Mappers.toEntity
import com.wuubzi.auth.infrastructure.Repositories.UserCredentialsRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserCredentialsRepositoryAdapter(
    private val userCredentialsRepository: UserCredentialsRepository
): UserCredentialsRepositoryPort{
    override fun save(userCredentials: UserCredentials): UserCredentials = userCredentialsRepository.save(userCredentials.toEntity()).toDomain()

    override fun findByEmail(email: String?): UserCredentials?  = userCredentialsRepository.findByEmail(email)?.toDomain()

    override fun findByUserId(userId: UUID): UserCredentials? = userCredentialsRepository.findByUserId(userId)?.toDomain()
}