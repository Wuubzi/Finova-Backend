package com.wuubzi.auth.infrastructure.Persistence.DTOS.Response

data class ErrorResponse (
    var message: String? = null,
    var code: Int? = null,
    var exception: String? = null,
    var path: String? = null
)