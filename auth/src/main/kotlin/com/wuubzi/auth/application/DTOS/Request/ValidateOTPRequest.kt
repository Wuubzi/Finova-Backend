package com.wuubzi.auth.application.DTOS.Request

data class ValidateOTPRequest (
    val email: String,
    val otp: String
)