package com.wuubzi.auth.application.Ports.`in`

import com.wuubzi.auth.application.DTOS.Request.UserRequest
import com.wuubzi.auth.domain.models.UserCredentials
import org.springframework.web.multipart.MultipartFile

fun interface CreateUserUseCase {
    fun createUser(userRequest: UserRequest, profile: MultipartFile): UserCredentials
}