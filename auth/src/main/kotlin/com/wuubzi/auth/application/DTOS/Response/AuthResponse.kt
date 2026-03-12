package com.wuubzi.auth.application.DTOS.Response

data class AuthResponse(
    val message: String,
    val url: String,
    val token: String,
    val code: Int,
    var timestamp: String
)
