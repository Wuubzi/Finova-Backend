package com.wuubzi.auth.TestUnits.Utils

import com.wuubzi.auth.Utils.DateFormatter
import org.junit.jupiter.api.Test

class DateFormatterTest {
    private val dateFormatter = DateFormatter()

    @Test
    fun `getDate should return a formatted date string`() {
        val date = dateFormatter.getDate()
        assert(date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) { "Date should be in YYYY-MM-DD format" }
    }
}