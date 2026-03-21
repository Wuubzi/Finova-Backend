package com.wuubzi.user.application.DTOS.Events

import java.util.UUID

data class UserRequestEvent(
    val idUser: UUID,
    val firstName: String,
    val lastName: String,
    val documentNumber: String,
    val phoneNumber: String,
    val profileUrl: String? = "",
    val address: String,
)
