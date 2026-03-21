package com.wuubzi.notification.DTOS.Events


data class RecoverPasswordRequest (
    val email: String,
    val otp: String,
)