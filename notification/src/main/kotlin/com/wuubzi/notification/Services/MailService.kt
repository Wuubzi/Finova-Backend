package com.wuubzi.notification.Services


import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class MailService(
    private val mailSender: JavaMailSender
) {
    fun sendMail() {
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper: MimeMessageHelper = MimeMessageHelper(message, "UTF-8")
        helper.setTo("carlosasalas321@gmail.com")
        helper.setSubject("Test email")
        helper.setText("This is a test email", true)
        mailSender.send(message)
    }
}