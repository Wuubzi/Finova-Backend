package com.wuubzi.auth.application.DTOS.Response

data class TokenResponse (
    val accessToken: String,
    val refreshToken: String
    )