package com.wuubzi.auth.Utils

import org.junit.jupiter.api.Test

class DateFormatterTest {
    private val dateFormatter = DateFormatter()

    @Test
    fun getDateShouldReturnAFormattedDateString() {
        val date = dateFormatter.getDate()
        assert(date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) { "Date should be in YYYY-MM-DD format" }
    }
}