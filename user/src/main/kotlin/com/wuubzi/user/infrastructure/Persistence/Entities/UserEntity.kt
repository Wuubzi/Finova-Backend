package com.wuubzi.user.infrastructure.Persistence.Entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp
import java.util.UUID


@Entity
@Table(name = "users")
class UserEntity {
    @Id
    @Column(name = "id_user")
    var idUser: UUID? = null
    @Column(name = "first_name")
    var firstName: String? = null
    @Column(name = "last_name")
    var lastName: String? = null
    @Column(name = "document_number")
    var documentNumber: String? = null
    var phone: String? = null
    @Column(name = "profile_url")
    var profileUrl: String? = null
    var address: String? = null
    @Column(name = "created_at")
    var createdAt: Timestamp? = null

}