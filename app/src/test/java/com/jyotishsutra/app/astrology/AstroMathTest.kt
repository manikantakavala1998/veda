package com.jyotishsutra.app.astrology

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class AstroMathTest {
    @Test
    fun `J2000 epoch converts to expected Julian day`() {
        val details = BirthDetails(
            name = "J2000",
            date = LocalDate.of(2000, 1, 1),
            time = LocalTime.NOON,
            zoneOffsetMinutes = 0,
            latitude = 0.0,
            longitude = 0.0,
        )
        assertEquals(2_451_545.0, julianDay(details), 1e-8)
    }

    @Test
    fun `degree normalization always returns zodiac range`() {
        assertEquals(350.0, normalizeDegrees(-10.0), 1e-10)
        assertEquals(1.0, normalizeDegrees(721.0), 1e-10)
        assertTrue(normalizeDegrees(-100_000.0) in 0.0..<360.0)
    }
}
