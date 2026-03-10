package com.wuubzi.auth.application.Ports.`in`

interface LogoutUseCase {
    fun logout(refreshToken: String)
}