package com.wuubzi.auth.infrastructure.Persistence.Entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp

@Entity
@Table(name = "user_credentials")
class UserCredentialsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
     @Column(name = "user_id")
    var userId: Long? = null
    var email: String? = null
    var password: String? = null
    var role: String? = null
    @Column(name = "is_active")
    var isActive: Boolean? = null
    @Column(name = "created_at")
    var createdAt: Timestamp? = null
}