package com.wuubzi.user.application.Ports.Out

import com.wuubzi.user.application.DTOS.Events.UserDeletedEvent

interface KafkaPort {
    fun publishUserDeleted(user: UserDeletedEvent)
}