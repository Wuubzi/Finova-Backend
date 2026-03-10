package com.wuubzi.auth.application.Ports.`in`

fun interface RecoverPasswordUseCase {
    fun recoverPassword(email: String)
}