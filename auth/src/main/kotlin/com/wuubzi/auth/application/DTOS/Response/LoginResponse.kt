package com.wuubzi.auth.application.DTOS.Response

data class LoginResponse(
    val message: String,
    val url: String,
    val token: String,
    val refreshToken: String,
    val code: Int,
    var timestamp: String
)
