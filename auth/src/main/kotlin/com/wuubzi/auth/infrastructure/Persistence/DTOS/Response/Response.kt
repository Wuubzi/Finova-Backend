package com.wuubzi.auth.infrastructure.Persistence.DTOS.Response

data class Response(
    var message: String? = null,
    var url: String? = null,
    var code: Int? = null,
    var timestamp: String? = null
)