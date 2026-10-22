package com.jyotishsutra.app.astrology

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class CompatibilityCalculatorTest {
    @Test
    fun `Ashtakoota exposes eight independently auditable scores`() {
        val first = details("A", LocalDate.of(1990, 5, 15))
        val second = details("B", LocalDate.of(1992, 8, 21))
        val result = CompatibilityCalculator().calculate(first, second)
        assertEquals(8, result.scores.size)
        assertEquals(36.0, result.scores.sumOf { it.maximum }, 0.0)
        assertTrue(result.total in 0.0..36.0)
    }

    private fun details(name: String, date: LocalDate) = BirthDetails(
        name = name,
        date = date,
        time = LocalTime.NOON,
        zoneOffsetMinutes = 330,
        latitude = 28.6139,
        longitude = 77.2090,
    )
}
