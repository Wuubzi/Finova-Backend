package com.wuubzi.user.application.Ports.Out

import com.wuubzi.user.application.DTOS.Events.UserDeletedEvent

fun interface KafkaPort {
    fun publishUserDeleted(user: UserDeletedEvent)
}