package com.wuubzi.auth.infrastructure.Adapters

import com.wuubzi.auth.Utils.FileValidation
import com.wuubzi.auth.application.Ports.out.FileValidationPort
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class FileValidationAdapter(
 private val fileValidation: FileValidation
): FileValidationPort{
 override fun validate(file: MultipartFile){
     return fileValidation.validateImageFile(file)
 }
}