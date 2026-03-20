package com.wuubzi.auth.infrastructure.Services

import io.awspring.cloud.s3.S3Template
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class S3Service(
    private val s3Template: S3Template,
    @Value($$"${aws.s3.bucket}")
    private val bucketName: String
) {
    fun uploadFile(file: MultipartFile): String {
       val filename = "${UUID.randomUUID()}_${file.originalFilename}"

        s3Template.upload(bucketName, filename, file.inputStream)
        println(bucketName)
        return "https://$bucketName.s3.amazonaws.com/$filename"
    }

}