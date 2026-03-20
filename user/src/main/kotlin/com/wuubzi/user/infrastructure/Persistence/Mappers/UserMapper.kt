package com.wuubzi.user.infrastructure.Persistence.Mappers

import com.wuubzi.user.domain.Models.User
import com.wuubzi.user.infrastructure.Persistence.Entities.UserEntity


fun User.toEntity(): UserEntity {
    return UserEntity().apply {
        idUser = this@toEntity.idUser
        firstName = this@toEntity.firstName
        lastName = this@toEntity.lastName
        documentNumber = this@toEntity.documentNumber
        phone = this@toEntity.phone
        profileUrl = this@toEntity.profileUrl
        address = this@toEntity.address
        createdAt  = this@toEntity.createdAt
    }
}


fun UserEntity.toDomain(): User {
    return User(
        idUser = idUser!!,
        firstName = firstName!!,
        lastName = lastName!!,
        documentNumber = documentNumber!!,
        phone = phone!!,
        profileUrl = profileUrl!!,
        address = address!!,
        createdAt = createdAt!!
    )
}