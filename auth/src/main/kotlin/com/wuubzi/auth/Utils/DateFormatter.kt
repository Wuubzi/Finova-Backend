package com.wuubzi.auth.Utils

import org.springframework.stereotype.Component
import java.text.SimpleDateFormat

@Component
class DateFormatter {

    fun getDate() : String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(System.currentTimeMillis())
    }
}