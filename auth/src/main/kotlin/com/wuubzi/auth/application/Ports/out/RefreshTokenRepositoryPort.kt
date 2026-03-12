package com.wuubzi.auth.application.Ports.out

import com.wuubzi.auth.domain.models.RefreshToken

interface RefreshTokenRepositoryPort {
    fun save(refreshToken: RefreshToken): RefreshToken
    fun findByToken(token: String): RefreshToken?
}