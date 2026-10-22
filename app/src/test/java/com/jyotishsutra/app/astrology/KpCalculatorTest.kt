package com.jyotishsutra.app.astrology

import org.junit.Assert.assertEquals
import org.junit.Test

class KpCalculatorTest {
    @Test
    fun `start of Ashwini is ruled by Ketu at all three levels`() {
        val result = KpCalculator().lordsAt(0.0)
        assertEquals("Ashwini", result.nakshatra)
        assertEquals(PlanetId.KETU, result.starLord)
        assertEquals(PlanetId.KETU, result.subLord)
        assertEquals(PlanetId.KETU, result.subSubLord)
    }

    @Test
    fun `start of Bharani uses Venus sequence`() {
        val result = KpCalculator().lordsAt(360.0 / 27.0)
        assertEquals("Bharani", result.nakshatra)
        assertEquals(PlanetId.VENUS, result.starLord)
        assertEquals(PlanetId.VENUS, result.subLord)
    }
}
