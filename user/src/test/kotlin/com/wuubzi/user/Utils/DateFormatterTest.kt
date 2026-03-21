package com.wuubzi.user.Utils

import com.wuubzi.user.utils.DateFormatter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DateFormatterTest {

    private lateinit var dateFormatter: DateFormatter

    @BeforeEach
    fun setup() {
        dateFormatter = DateFormatter()
    }

    @Test
    fun shouldReturnDateInCorrectFormat() {
        // WHEN
        val result = dateFormatter.getDate()

        // THEN: Verificamos el formato yyyy-MM-dd (ej: 2026-03-20)
        // La regex busca: 4 dígitos - 2 dígitos - 2 dígitos
        val regex = Regex("""^\d{4}-\d{2}-\d{2}$""")

        assertTrue(result.matches(regex), "El formato de fecha '$result' no coincide con yyyy-MM-dd")
    }

    @Test
    fun shouldReturnLengthOfTen() {
        // WHEN
        val result = dateFormatter.getDate()

        // THEN: El formato yyyy-MM-dd siempre debe tener 10 caracteres
        assert(result.length == 10)
    }
}