package com.wuubzi.user.infrastructure.Adapters

import com.wuubzi.user.application.Ports.Out.UserRepositoryPort
import com.wuubzi.user.domain.Models.User
import com.wuubzi.user.infrastructure.Persistence.Entities.UserEntity
import com.wuubzi.user.infrastructure.Persistence.Mappers.toDomain
import com.wuubzi.user.infrastructure.Persistence.Mappers.toEntity
import com.wuubzi.user.infrastructure.Repositories.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class  UserRepositoryAdapter(
    private val userRepository: UserRepository
): UserRepositoryPort {
    override fun save(user: User) = userRepository.save(user.toEntity()).toDomain()
    @Transactional
    override fun delete(userId: UUID) = userRepository.deleteByIdUser(userId)
    override fun findByIdUser(userId: UUID): User?  = userRepository.findByIdUser(userId)?.toDomain()
    override fun findByDocumentNumber(documentNumber: String): User? = userRepository.findByDocumentNumber(documentNumber)?.toDomain()
}