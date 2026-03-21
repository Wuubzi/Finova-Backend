package com.wuubzi.user.application.DTOS.Request

data class UpdateRequestDTO (
    val firstName: String,
    val lastName: String,
    val documentNumber: String,
    val phone: String,
    val address: String,
)