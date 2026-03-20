package com.wuubzi.user.domain.Models

import java.sql.Timestamp
import java.util.UUID

data class User(
    val idUser: UUID,
    val firstName: String,
    val lastName: String,
    val documentNumber: String,
    val phone: String,
    val profileUrl: String,
    val address: String,
    val createdAt: Timestamp,
)
