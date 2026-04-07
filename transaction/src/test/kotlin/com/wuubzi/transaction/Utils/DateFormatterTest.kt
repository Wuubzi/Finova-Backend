package com.wuubzi.transaction.Utils

import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DateFormatterTest {

    private val dateFormatter = DateFormatter()

    @Test
    fun shouldReturnFormattedDate() {
        // WHEN
        val result = dateFormatter.getDate()

        // THEN
        assertNotNull(result)
        assertTrue(result.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun shouldReturnCurrentDate() {
        // GIVEN
        val expected = SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis())

        // WHEN
        val result = dateFormatter.getDate()

        // THEN
        assertEquals(expected, result)
    }
}

