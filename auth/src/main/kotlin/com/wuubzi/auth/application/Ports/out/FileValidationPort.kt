package com.wuubzi.auth.application.Ports.out

import org.springframework.web.multipart.MultipartFile

interface FileValidationPort {
    fun validate(file: MultipartFile)
}