package com.wuubzi.auth.Utils

import com.wuubzi.auth.infrastructure.Exceptions.InvalidFileException
import com.wuubzi.auth.infrastructure.Exceptions.MaliciousFileException
import com.wuubzi.auth.infrastructure.Exceptions.VirusScanException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.imageio.ImageIO

@Component
class FileValidation(
    @Value($$"${virustotal.api.key}")
    private val virusTotalApiKey: String
) {
    companion object {
        private val ALLOWED_EXTENSIONS = setOf("png", "jpg", "jpeg")
    }

    fun validateImageFile(file: MultipartFile) {
       if (!isValidImage(file)) {
            throw InvalidFileException("File is not a valid PNG or JPG image")
       }


        scanFileWithVirusTotal(file)
    }

    private fun isValidImage(file: MultipartFile): Boolean {
        if (file.isEmpty) return false


        val extension = file.originalFilename
            ?.substringAfterLast('.', "")
            ?.lowercase() ?: return false

        if (extension !in ALLOWED_EXTENSIONS) return false


        return try {
            file.inputStream.use { inputStream ->
                ImageIO.read(inputStream) != null
            }
        } catch (e: IOException) {
            false
        }
    }

    private fun scanFileWithVirusTotal(file: MultipartFile) {
        try {
            val boundary = "----WebKitFormBoundary${System.currentTimeMillis()}"
            val client = HttpClient.newBuilder().build()

            val header = """
                --$boundary
                Content-Disposition: form-data; name="file"; filename="${file.originalFilename}"
                Content-Type: ${file.contentType}
                
                
            """.trimIndent().replace("\n", "\r\n")

            val footer = "\r\n--$boundary--\r\n"
            val body = header.toByteArray() + file.bytes + footer.toByteArray()


            val uploadRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://www.virustotal.com/api/v3/files"))
                .header("x-apikey", virusTotalApiKey)
                .header("Content-Type", "multipart/form-data; boundary=$boundary")
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build()

            val uploadResponse = client.send(uploadRequest, HttpResponse.BodyHandlers.ofString())


            println("VirusTotal Upload Response Status: ${uploadResponse.statusCode()}")
            println("VirusTotal Upload Response Body: ${uploadResponse.body()}")


            if (uploadResponse.statusCode() != 200) {
                throw VirusScanException("Virus scan failed")
            }


            val responseBody = uploadResponse.body()
            val analysisIdRegex = Regex("\"data\":\\s*\\{[^}]*\"id\":\\s*\"([^\"]+)\"")
            val matchResult = analysisIdRegex.find(responseBody)

            val analysisId = matchResult?.groupValues?.get(1)
                ?: throw VirusScanException("Could not extract analysis ID from response: $responseBody")

            println("Analysis ID: $analysisId")

            // Esperar para que complete el análisis
            Thread.sleep(20000) // 20 segundos

            val resultsRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://www.virustotal.com/api/v3/analyses/$analysisId"))
                .header("x-apikey", virusTotalApiKey)
                .GET()
                .build()

            val resultsResponse = client.send(resultsRequest, HttpResponse.BodyHandlers.ofString())

            println("VirusTotal Results Response Status: ${resultsResponse.statusCode()}")
            println("VirusTotal Results Response Body: ${resultsResponse.body()}")

            if (resultsResponse.statusCode() != 200) {
                throw VirusScanException("Could not get analysis results: ${resultsResponse.body()}")
            }

            // Verificar si el análisis está completo
            val status = Regex("\"status\":\\s*\"([^\"]+)\"")
                .find(resultsResponse.body())
                ?.groupValues?.get(1)

            println("Analysis Status: $status")

            // Extraer conteo de maliciosos
            val statsRegex = Regex("\"stats\":\\s*\\{[^}]*\"malicious\":\\s*(\\d+)")
            val maliciousCount = statsRegex
                .find(resultsResponse.body())
                ?.groupValues?.get(1)
                ?.toInt() ?: 0

            println("Malicious Count: $maliciousCount")

            if (maliciousCount > 0) {
                throw MaliciousFileException("File contains malicious content detected by $maliciousCount antivirus engines")
            }


        } catch (e: MaliciousFileException) {
            throw e
        } catch (e: VirusScanException) {
            throw e
        } catch (e: Exception) {
            throw VirusScanException("Virus scan error: ${e.message}")
        }
    }
}