package com.wuubzi.auth.infrastructure.Persistence.Entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "refresh_tokens")
class RefreshTokenEntity {
    @Id
    var id: UUID? = null
    @Column(name = "user_id")
    var userId: UUID? = null
    var token: String? = null
    @Column(name = "expires_at")
    var expiresAt: Instant? = null
    @Column(name = "is_revoked")
    var isRevoked: Boolean? = null
    @Column(name = "created_at")
    var createdAt: Instant = Instant.now()
}