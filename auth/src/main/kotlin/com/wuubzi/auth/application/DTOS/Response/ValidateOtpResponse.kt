package com.wuubzi.auth.application.DTOS.Response

data class ValidateOtpResponse (
    val message: String,
    val url: String,
    val resetToken: String,
    val code: Int,
    val timestamp: String
)