package com.wuubzi.auth.Utils

import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.stream.Collectors
import java.util.stream.IntStream

private const val OTP_LENGTH = 6
@Component
class OtpGenerator {

    private val secureRandom = SecureRandom()

    public fun generateOtp(): String {
        return IntStream.range(0, OTP_LENGTH)
            .mapToObj { _ -> secureRandom.nextInt(10).toString() }
            .collect(Collectors.joining())
    }
}