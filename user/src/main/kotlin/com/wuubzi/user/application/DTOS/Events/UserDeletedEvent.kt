package com.wuubzi.user.application.DTOS.Events

import java.util.UUID

data class UserDeletedEvent (
    val idUser: UUID
)