package com.wuubzi.auth.infrastructure.Adapters

import com.wuubzi.auth.application.Ports.out.RefreshTokenRepositoryPort
import com.wuubzi.auth.domain.models.RefreshToken
import com.wuubzi.auth.infrastructure.Persistence.Mappers.toDomain
import com.wuubzi.auth.infrastructure.Persistence.Mappers.toEntity
import com.wuubzi.auth.infrastructure.Repositories.RefreshTokenRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class RefreshTokenRepositoryAdapter(
    private val refreshTokenRepository: RefreshTokenRepository
): RefreshTokenRepositoryPort {
    override fun save(refreshToken: RefreshToken): RefreshToken = refreshTokenRepository.save(refreshToken.toEntity()).toDomain()

    override fun findByToken(token: String?): RefreshToken? {
        val refreshToken = refreshTokenRepository.findByToken(token)
        return refreshToken?.toDomain()
    }

    override fun findByUserId(userId: UUID): RefreshToken? {
        val refreshToken = refreshTokenRepository.findByUserId(userId)
        return refreshToken?.toDomain()
    }

}