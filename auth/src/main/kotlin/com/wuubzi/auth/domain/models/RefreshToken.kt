package com.wuubzi.auth.domain.models

import java.time.Instant
import java.util.UUID

data class RefreshToken (
    val id: UUID,
    val userId: UUID,
    val token: String,
    val expiresAt: Instant,
    val isRevoked: Boolean,
    val createdAt: Instant
)