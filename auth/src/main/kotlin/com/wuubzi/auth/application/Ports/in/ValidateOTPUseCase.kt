package com.wuubzi.auth.application.Ports.`in`

import com.wuubzi.auth.application.DTOS.Request.ValidateOTPRequest

fun interface ValidateOTPUseCase {
    fun validateOTP(validateOtpRequest: ValidateOTPRequest): String
}