package com.wuubzi.auth.application.DTOS.Request

data class LoginRequest(
    val email: String,
    val password: String,
)
