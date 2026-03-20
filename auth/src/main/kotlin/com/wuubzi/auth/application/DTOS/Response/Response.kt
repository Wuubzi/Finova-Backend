package com.wuubzi.auth.application.DTOS.Response

data class Response(
    val message: String,
    val url: String,
    val code: Int,
    val timestamp: String
)