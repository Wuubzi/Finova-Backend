package com.wuubzi.auth.application.Ports.out

import org.springframework.web.multipart.MultipartFile

fun interface BucketPort {
    fun saveBucket(profile: MultipartFile): String
}