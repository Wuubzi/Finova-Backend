package com.wuubzi.auth.application.Ports.out

import org.springframework.web.multipart.MultipartFile

fun interface FileValidationPort {
    fun validate(file: MultipartFile)
}