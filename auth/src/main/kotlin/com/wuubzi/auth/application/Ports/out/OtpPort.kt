package com.wuubzi.auth.application.Ports.out

fun interface OtpPort {
    fun generateOtp(): String
}