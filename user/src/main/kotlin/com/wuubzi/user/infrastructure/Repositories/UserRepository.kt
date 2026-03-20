package com.wuubzi.user.infrastructure.Repositories

import com.wuubzi.user.infrastructure.Persistence.Entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository: JpaRepository<UserEntity, Long> {
    fun findByDocumentNumber(documentNumber: String): UserEntity?
    fun findByIdUser(idUser: UUID): UserEntity?
    fun deleteByIdUser(idUser: UUID)
}