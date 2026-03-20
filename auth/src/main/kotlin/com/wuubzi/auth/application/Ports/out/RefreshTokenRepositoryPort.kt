package com.wuubzi.auth.application.Ports.out

import com.wuubzi.auth.domain.models.RefreshToken
import java.util.UUID

interface RefreshTokenRepositoryPort {
    fun save(refreshToken: RefreshToken): RefreshToken
    fun findByToken(token: String?): RefreshToken?
    fun findByUserId(userId: UUID): RefreshToken?
}