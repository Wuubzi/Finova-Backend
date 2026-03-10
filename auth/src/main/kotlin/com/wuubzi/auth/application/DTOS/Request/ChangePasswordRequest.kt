package com.wuubzi.auth.application.DTOS.Request

data class ChangePasswordRequest(
    val email: String,
    val resetToken: String,
    val password: String,
    val confirmPassword: String,
)