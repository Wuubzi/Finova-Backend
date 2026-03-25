package com.wuubzi.account.application.DTOS.Events

import java.util.UUID

data class UserDeletedEvent (
    val idUser: UUID,
)