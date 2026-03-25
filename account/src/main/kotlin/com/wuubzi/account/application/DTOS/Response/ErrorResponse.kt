package com.wuubzi.account.application.DTOS.Response

data class ErrorResponse (
    val message: String?,
    val code: Int,
    val exception: String,
    val path: String
)