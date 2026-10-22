package com.jyotishsutra.app.astrology

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class HoroscopeCalculatorTest {
    private val sample = BirthDetails(
        name = "Sample",
        date = LocalDate.of(1990, 5, 15),
        time = LocalTime.of(10, 30),
        zoneOffsetMinutes = 330,
        latitude = 28.6139,
        longitude = 77.2090,
        placeName = "New Delhi",
    )

    @Test
    fun `chart contains all navagraha with valid derived values`() {
        val chart = HoroscopeCalculator().calculate(sample)
        assertEquals(9, chart.planets.size)
        chart.planets.forEach { planet ->
            assertTrue(planet.siderealLongitude in 0.0..<360.0)
            assertTrue(planet.signIndex in 0..11)
            assertTrue(planet.nakshatraIndex in 0..26)
            assertTrue(planet.pada in 1..4)
            assertTrue(planet.house in 1..12)
        }
        assertTrue(chart.ascendant.siderealLongitude in 0.0..<360.0)
    }

    @Test
    fun `nodes remain exactly opposite`() {
        val chart = HoroscopeCalculator().calculate(sample)
        val rahu = chart.planets.first { it.id == PlanetId.RAHU }.siderealLongitude
        val ketu = chart.planets.first { it.id == PlanetId.KETU }.siderealLongitude
        assertEquals(180.0, kotlin.math.abs(signedDegrees(rahu - ketu)), 1e-8)
    }

    @Test
    fun `one complete Vimshottari cycle totals 120 years`() {
        val chart = HoroscopeCalculator().calculate(sample)
        assertEquals(120, chart.vimshottari.take(9).sumOf { it.years })
    }

    @Test
    fun `New Delhi golden chart stays close to high precision reference`() {
        val chart = HoroscopeCalculator().calculate(sample)
        val expected = mapOf(
            PlanetId.SUN to 30.3891,
            PlanetId.MOON to 269.8422,
            PlanetId.MARS to 324.3914,
            PlanetId.MERCURY to 14.3250,
            PlanetId.JUPITER to 75.7608,
            PlanetId.VENUS to 348.7599,
            PlanetId.SATURN to 271.5285,
            PlanetId.RAHU to 287.6288,
        )
        expected.forEach { (planet, longitude) ->
            val actual = chart.planets.first { it.id == planet }.siderealLongitude
            assertEquals("$planet longitude", longitude, actual, 0.15)
        }
        assertEquals(99.8847, chart.ascendant.siderealLongitude, 0.15)
    }
}
