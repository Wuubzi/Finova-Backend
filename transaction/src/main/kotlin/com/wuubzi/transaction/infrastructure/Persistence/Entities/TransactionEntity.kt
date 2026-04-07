package com.wuubzi.transaction.infrastructure.Persistence.Entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp
import java.util.UUID

@Entity
@Table(name = "transaction")
class TransactionEntity {
    @Id
    @Column(name = "transaction_id")
    var transactionId: UUID? =  UUID.randomUUID()
    @Column(name = "from_account_id")
    var fromAccountId: UUID? = null
    @Column(name = "to_account_id")
    var toAccountId: UUID? = null
    var amount: Double? = null
    var currency: String? = null
    var status: String? = null
    var type: String? = null
    var description: String? = null
    @Column(name = "created_at")
    var createdAt: Timestamp? = null
}