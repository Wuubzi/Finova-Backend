package com.wuubzi.auth.application.Ports.out

interface PasswordEncoderPort {
    fun encode(password: String): String
    fun matches(rawPassword: String, encodedPassword: String): Boolean
}