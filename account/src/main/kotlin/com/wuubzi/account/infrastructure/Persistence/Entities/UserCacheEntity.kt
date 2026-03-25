package com.wuubzi.account.infrastructure.Persistence.Entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "user_cache")
class UserCacheEntity {
    @Id
    var userId: UUID? = UUID.randomUUID()
}