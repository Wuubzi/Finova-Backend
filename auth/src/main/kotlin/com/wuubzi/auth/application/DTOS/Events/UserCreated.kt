package com.wuubzi.auth.application.DTOS.Events

import java.util.UUID

data class UserCreated(
    val idUser: UUID,
    val firstName: String,
    val lastName: String,
    val documentNumber: String,
    val phoneNumber: String,
    val address: String,
)
