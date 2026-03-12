package com.wuubzi.auth.domain.models

import java.sql.Timestamp
import java.util.UUID

data class UserCredentials(
    val id: UUID,
    val userId: UUID,
    val email: String,
    val password: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: Timestamp,
    )
