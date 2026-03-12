package com.wuubzi.auth.application.Ports.out

import com.wuubzi.auth.application.DTOS.Events.UserCreated

interface KafkaPort {
    fun publishUserCreated(user: UserCreated)
    fun publishRecoverPassword(otp: String)
}