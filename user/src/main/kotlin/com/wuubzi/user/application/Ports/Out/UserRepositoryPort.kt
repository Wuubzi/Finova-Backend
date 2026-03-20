package com.wuubzi.user.application.Ports.Out

import com.wuubzi.user.domain.Models.User
import java.util.UUID

interface UserRepositoryPort {
    fun save(user: User): User
    fun delete(userId: UUID)
    fun findByIdUser(userId: UUID): User?
    fun findByDocumentNumber(documentNumber: String): User?
}