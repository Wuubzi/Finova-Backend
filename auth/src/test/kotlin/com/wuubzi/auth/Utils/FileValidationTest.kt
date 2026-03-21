package com.wuubzi.auth.Utils

import com.wuubzi.auth.infrastructure.Exceptions.InvalidFileException
import com.wuubzi.auth.infrastructure.Exceptions.VirusScanException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.web.multipart.MultipartFile
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class FileValidationTest {

    private lateinit var fileValidation: FileValidation
    private val apiKey = "test-api-key"

    @Mock
    lateinit var multipartFile: MultipartFile


    @BeforeEach
    fun setUp() {
        fileValidation = FileValidation(apiKey)
    }

    @Test
    fun shouldThrowInvalidFileExceptionWhenFileIsEmpty() {
        whenever(multipartFile.isEmpty).thenReturn(true)

        assertThrows(InvalidFileException::class.java) {
            fileValidation.validateImageFile(multipartFile)
        }
    }

    @Test
    fun shouldThrowInvalidFileExceptionWhenExtensionIsNotAllowed() {
        whenever(multipartFile.isEmpty).thenReturn(false)
        whenever(multipartFile.originalFilename).thenReturn("virus.exe")

        assertThrows(InvalidFileException::class.java) {
            fileValidation.validateImageFile(multipartFile)
        }
    }
    @Test
    fun shouldThrowVirusScanExceptionWhenAPIReturnsnon200Status() {
        // GIVEN
        val filename = "test.jpg"
        whenever(multipartFile.isEmpty).thenReturn(false)
        whenever(multipartFile.originalFilename).thenReturn(filename)

        // Creamos un stream de una imagen real de 1x1 para que ImageIO.read no falle
        val out = java.io.ByteArrayOutputStream()
        val img = java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB)
        javax.imageio.ImageIO.write(img, "jpg", out)
        val imageBytes = out.toByteArray()

        whenever(multipartFile.inputStream).thenReturn(java.io.ByteArrayInputStream(imageBytes))
        whenever(multipartFile.bytes).thenReturn(imageBytes)
        whenever(multipartFile.contentType).thenReturn("image/jpeg")


        assertThrows(VirusScanException::class.java) {
            fileValidation.validateImageFile(multipartFile)
        }
    }
}