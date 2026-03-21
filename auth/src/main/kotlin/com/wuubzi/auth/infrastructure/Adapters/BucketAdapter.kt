package com.wuubzi.auth.infrastructure.Adapters

import com.wuubzi.auth.application.Ports.out.BucketPort
import com.wuubzi.auth.infrastructure.Services.S3Service
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class BucketAdapter(
    private val s3Service: S3Service
): BucketPort {
    override fun saveBucket(profile: MultipartFile): String = s3Service.uploadFile(profile)
}