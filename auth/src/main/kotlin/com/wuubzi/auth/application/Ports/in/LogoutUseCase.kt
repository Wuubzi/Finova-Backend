package com.wuubzi.auth.application.Ports.`in`

fun interface LogoutUseCase {
    fun logout(refreshToken: String)
}