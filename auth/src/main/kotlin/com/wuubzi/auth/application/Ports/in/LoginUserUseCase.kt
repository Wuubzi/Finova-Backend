package com.wuubzi.auth.application.Ports.`in`

import com.wuubzi.auth.application.DTOS.Request.LoginRequest
import com.wuubzi.auth.application.DTOS.Response.TokenResponse

interface LoginUserUseCase {
    fun login(userLogin: LoginRequest): TokenResponse
}