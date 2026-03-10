package com.wuubzi.auth.infrastructure.Persistence.Entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp
import java.util.UUID

@Entity
@Table(name = "user_credentials")
class UserCredentialsEntity {

    @Id
    var id: UUID? = null
     @Column(name = "user_id")
    var userId: UUID? = null
    var email: String? = null
    var password: String? = null
    var role: String? = null
    @Column(name = "is_active")
    var isActive: Boolean? = null
    @Column(name = "created_at")
    var createdAt: Timestamp? = null
}