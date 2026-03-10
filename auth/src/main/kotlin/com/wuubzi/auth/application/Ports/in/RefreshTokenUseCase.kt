package com.wuubzi.auth.application.Ports.`in`

import com.wuubzi.auth.application.DTOS.Request.RefreshTokenRequest

interface RefreshTokenUseCase {
    fun refreshToken(refreshTokenRequest: RefreshTokenRequest): String
}