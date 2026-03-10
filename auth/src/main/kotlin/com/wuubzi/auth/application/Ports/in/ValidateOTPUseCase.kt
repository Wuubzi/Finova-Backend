package com.wuubzi.auth.application.Ports.`in`

import com.wuubzi.auth.application.DTOS.Request.ValidateOTPRequest

interface ValidateOTPUseCase {
    fun validateOTP(validateOtpRequest: ValidateOTPRequest): String
}