package com.wuubzi.auth.application.Ports.`in`

import com.wuubzi.auth.application.DTOS.Request.ChangePasswordRequest

fun interface ChangePasswordUseCase {
    fun changePassword(changePasswordRequest: ChangePasswordRequest)
}