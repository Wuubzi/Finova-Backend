package com.wuubzi.user.application.Ports.In

import com.wuubzi.user.application.DTOS.Request.UpdateRequestDTO
import java.util.UUID


interface UpdateUserUseCase {
    fun updateUser(userId: UUID, userRequest: UpdateRequestDTO)
}