package com.wuubzi.auth.domain.models

import java.sql.Timestamp

data class UserCredentials(
    val id: Long?,
    val userId: Long?,
    val email: String?,
    val password: String?,
    val role: String?,
    val isActive: Boolean,
    val createdAt: Timestamp?,
    )
