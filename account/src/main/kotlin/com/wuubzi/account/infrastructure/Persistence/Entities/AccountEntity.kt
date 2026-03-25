package com.wuubzi.account.infrastructure.Persistence.Entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "account")
class AccountEntity {
    @Id
    @Column(name = "account_id")
    var idAccount: UUID = UUID.randomUUID()
    @Column(name = "account_number")
    var accountNumber: String = ""
    @Column(name = "user_id")
    var userId: UUID = UUID.randomUUID()
    @Column(name = "account_type")
    var accountType: String = ""
    var currency: String = ""
    var balance: Double = 0.0
    @Column(name = "available_balance")
    var availableBalance: Double = 0.0
    var status: String = ""
    var alias: String = ""
    @Column(name = "overdraft_limit")
    var overdraftLimit: Double = 0.0
    @Column(name = "created_at")
    var createdAt: Timestamp = Timestamp.from(Instant.now())
    @Column(name = "updated_at")
    var updatedAt: Timestamp? = null

}