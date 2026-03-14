package com.wuubzi.auth.application.DTOS.Events

data class RecoverPassword (
    val email: String,
    val otp: String,
)