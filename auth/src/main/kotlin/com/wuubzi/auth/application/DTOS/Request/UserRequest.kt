package com.wuubzi.auth.application.DTOS.Request

data class UserRequest(
    val firstName: String,
    val lastName: String,
    val documentNumber: String,
    val phoneNumber: String,
    val address: String,
    val email: String,
    val password: String,
)
