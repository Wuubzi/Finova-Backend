package com.wuubzi.auth.application.Ports.out

interface OtpPort {
    fun generateOtp(): String
}