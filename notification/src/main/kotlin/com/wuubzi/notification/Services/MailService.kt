package com.wuubzi.notification.Services


import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class MailService(
    private val mailSender: JavaMailSender
) {

    fun sendRecoverPasswordEmail(email: String, otp: String) {
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        helper.setTo(email)
        helper.setSubject("Password Recovery - Verification Code")
        helper.setText(buildRecoverPasswordHtml(otp), true)

        mailSender.send(message)
    }

    fun sendTransactionEmail(
        email: String,
        transactionId: String,
        type: String,
        eventType: String,
        amount: Double,
        currency: String,
        description: String?
    ) {
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        val (subject, statusLabel, statusColor, statusIcon) = when (eventType) {
            "TRANSACTION_CREATED" -> listOf("Transaction In Progress", "In Progress", "#f59e0b", "⏳")
            "TRANSACTION_COMPLETED" -> listOf("Transaction Successful", "Completed", "#10b981", "✅")
            "TRANSACTION_FAILED" -> listOf("Transaction Failed", "Failed", "#ef4444", "❌")
            else -> listOf("Transaction Update", "Unknown", "#6b7280", "ℹ️")
        }

        val typeLabel = when (type) {
            "DEPOSIT" -> "Deposit"
            "WITHDRAW" -> "Withdrawal"
            "TRANSFER" -> "Transfer"
            else -> type
        }

        helper.setTo(email)
        helper.setSubject("$statusIcon $subject - $typeLabel")
        helper.setText(buildTransactionHtml(transactionId, typeLabel, statusLabel, statusColor, statusIcon, amount, currency, description), true)

        mailSender.send(message)
    }

    private fun buildTransactionHtml(
        transactionId: String,
        typeLabel: String,
        statusLabel: String,
        statusColor: String,
        statusIcon: String,
        amount: Double,
        currency: String,
        description: String?
    ): String {
        val formattedAmount = "%,.2f".format(amount)
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Transaction Notification</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f5f5f5;">
                <table role="presentation" style="width: 100%; border-collapse: collapse;">
                    <tr>
                        <td align="center" style="padding: 40px 0;">
                            <table role="presentation" style="width: 600px; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                                
                                <!-- Header -->
                                <tr>
                                    <td style="padding: 40px 40px 20px 40px; text-align: center;">
                                        <h1 style="margin: 0; color: #333333; font-size: 28px; font-weight: 600;">
                                            💸 Transaction Notification
                                        </h1>
                                    </td>
                                </tr>
                                
                                <!-- Status Badge -->
                                <tr>
                                    <td align="center" style="padding: 10px 40px 20px 40px;">
                                        <span style="display: inline-block; background-color: ${statusColor}20; color: ${statusColor}; padding: 8px 20px; border-radius: 20px; font-size: 14px; font-weight: 600; border: 1px solid ${statusColor}40;">
                                            ${statusIcon} ${statusLabel}
                                        </span>
                                    </td>
                                </tr>
                                
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 10px 40px;">
                                        <p style="margin: 0 0 20px 0; color: #666666; font-size: 16px; line-height: 24px;">
                                            Hello,
                                        </p>
                                        <p style="margin: 0 0 20px 0; color: #666666; font-size: 16px; line-height: 24px;">
                                            Here are the details of your <strong>${typeLabel.lowercase()}</strong> transaction:
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Amount -->
                                <tr>
                                    <td align="center" style="padding: 10px 40px;">
                                        <table role="presentation" style="border-collapse: collapse;">
                                            <tr>
                                                <td style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 8px; padding: 20px 40px; text-align: center;">
                                                    <span style="color: #ffffffaa; font-size: 14px; font-weight: 500; display: block; margin-bottom: 4px;">Amount</span>
                                                    <span style="color: #ffffff; font-size: 32px; font-weight: 700; font-family: 'Courier New', monospace;">
                                                        ${formattedAmount} ${currency}
                                                    </span>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                
                                <!-- Transaction Details -->
                                <tr>
                                    <td style="padding: 20px 40px;">
                                        <table role="presentation" style="width: 100%; border-collapse: collapse; background-color: #f9fafb; border-radius: 8px;">
                                            <tr>
                                                <td style="padding: 15px 20px; border-bottom: 1px solid #e5e7eb;">
                                                    <span style="color: #9ca3af; font-size: 12px; text-transform: uppercase; font-weight: 600;">Transaction ID</span><br>
                                                    <span style="color: #374151; font-size: 13px; font-family: 'Courier New', monospace;">${transactionId}</span>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td style="padding: 15px 20px; border-bottom: 1px solid #e5e7eb;">
                                                    <span style="color: #9ca3af; font-size: 12px; text-transform: uppercase; font-weight: 600;">Type</span><br>
                                                    <span style="color: #374151; font-size: 14px; font-weight: 500;">${typeLabel}</span>
                                                </td>
                                            </tr>
                                            ${if (description != null) """
                                            <tr>
                                                <td style="padding: 15px 20px;">
                                                    <span style="color: #9ca3af; font-size: 12px; text-transform: uppercase; font-weight: 600;">Description</span><br>
                                                    <span style="color: #374151; font-size: 14px;">${description}</span>
                                                </td>
                                            </tr>
                                            """ else ""}
                                        </table>
                                    </td>
                                </tr>
                                
                                <!-- Divider -->
                                <tr>
                                    <td style="padding: 0 40px;">
                                        <hr style="border: none; border-top: 1px solid #eeeeee; margin: 20px 0;">
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="padding: 20px 40px 40px 40px; text-align: center;">
                                        <p style="margin: 0 0 10px 0; color: #999999; font-size: 12px; line-height: 18px;">
                                            This is an automated email, please do not reply to this message.
                                        </p>
                                        <p style="margin: 0; color: #999999; font-size: 12px; line-height: 18px;">
                                            © ${java.time.Year.now()} Finova. All rights reserved.
                                        </p>
                                    </td>
                                </tr>
                                
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.trimIndent()
    }

    private fun buildRecoverPasswordHtml(otp: String): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Recovery</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f5f5f5;">
                <table role="presentation" style="width: 100%; border-collapse: collapse;">
                    <tr>
                        <td align="center" style="padding: 40px 0;">
                            <table role="presentation" style="width: 600px; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                                
                                <!-- Header -->
                                <tr>
                                    <td style="padding: 40px 40px 20px 40px; text-align: center;">
                                        <h1 style="margin: 0; color: #333333; font-size: 28px; font-weight: 600;">
                                            🔐 Password Recovery
                                        </h1>
                                    </td>
                                </tr>
                                
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 20px 40px;">
                                        <p style="margin: 0 0 20px 0; color: #666666; font-size: 16px; line-height: 24px;">
                                            Hello,
                                        </p>
                                        <p style="margin: 0 0 20px 0; color: #666666; font-size: 16px; line-height: 24px;">
                                            We received a request to recover your password. Use the following verification code:
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- OTP Code -->
                                <tr>
                                    <td align="center" style="padding: 20px 40px;">
                                        <table role="presentation" style="border-collapse: collapse;">
                                            <tr>
                                                <td style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 8px; padding: 20px 40px;">
                                                    <span style="color: #ffffff; font-size: 32px; font-weight: 700; letter-spacing: 8px; font-family: 'Courier New', monospace;">
                                                        ${otp}
                                                    </span>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                
                                <!-- Info -->
                                <tr>
                                    <td style="padding: 20px 40px;">
                                        <p style="margin: 0 0 15px 0; color: #666666; font-size: 14px; line-height: 20px;">
                                            ⏰ <strong>This code expires in 10 minutes.</strong>
                                        </p>
                                        <p style="margin: 0 0 20px 0; color: #666666; font-size: 14px; line-height: 20px;">
                                            If you didn't request a password recovery, you can safely ignore this email.
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Divider -->
                                <tr>
                                    <td style="padding: 0 40px;">
                                        <hr style="border: none; border-top: 1px solid #eeeeee; margin: 20px 0;">
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="padding: 20px 40px 40px 40px; text-align: center;">
                                        <p style="margin: 0 0 10px 0; color: #999999; font-size: 12px; line-height: 18px;">
                                            This is an automated email, please do not reply to this message.
                                        </p>
                                        <p style="margin: 0; color: #999999; font-size: 12px; line-height: 18px;">
                                            © ${java.time.Year.now()} Wuubzi. All rights reserved.
                                        </p>
                                    </td>
                                </tr>
                                
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.trimIndent()
    }
}