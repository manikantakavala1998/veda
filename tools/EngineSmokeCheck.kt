package com.jyotishsutra.app.astrology

import java.time.LocalDate
import java.time.LocalTime

fun main() {
    val details = BirthDetails(
        name = "Validation chart",
        date = LocalDate.of(1990, 5, 15),
        time = LocalTime.of(10, 30),
        zoneOffsetMinutes = 330,
        latitude = 28.6139,
        longitude = 77.2090,
        placeName = "New Delhi, India",
    )
    val horoscope = HoroscopeCalculator().calculate(details)
    check(horoscope.planets.size == 9)
    check(horoscope.planets.all { it.siderealLongitude in 0.0..<360.0 })
    check(horoscope.planets.all { it.house in 1..12 })
    check(horoscope.vimshottari.take(9).sumOf { it.years } == 120)
    check(KpCalculator().calculate(horoscope).positions.size == 9)
    check(LalKitabCalculator().calculate(horoscope).houses.size == 12)
    check(PredictionEngine().generate(horoscope).isNotEmpty())

    val second = details.copy(name = "Second chart", date = LocalDate.of(1992, 8, 21))
    val match = CompatibilityCalculator().calculate(details, second)
    check(match.scores.size == 8)
    check(match.total in 0.0..36.0)
    verifyGolden(
        horoscope,
        mapOf(
            PlanetId.SUN to 30.3891,
            PlanetId.MOON to 269.8422,
            PlanetId.MARS to 324.3914,
            PlanetId.MERCURY to 14.3250,
            PlanetId.JUPITER to 75.7608,
            PlanetId.VENUS to 348.7599,
            PlanetId.SATURN to 271.5285,
            PlanetId.RAHU to 287.6288,
        ),
        expectedAscendant = 99.8847,
    )

    println("Engine smoke check passed")
    println("Julian day: ${horoscope.julianDay}")
    println("Ascendant: ${SIGN_NAMES[horoscope.ascendant.signIndex]} ${"%.2f".format(horoscope.ascendant.degreeInSign)}")
    horoscope.planets.forEach {
        println("${it.id.displayName}: ${SIGN_NAMES[it.signIndex]} ${"%.2f".format(it.degreeInSign)} house ${it.house}")
    }
    println("Panchang: ${horoscope.panchang}")
    println("Compatibility: ${match.total}/36")
}

private fun verifyGolden(
    horoscope: Horoscope,
    expected: Map<PlanetId, Double>,
    expectedAscendant: Double,
) {
    expected.forEach { (planet, longitude) ->
        val actual = horoscope.planets.first { it.id == planet }.siderealLongitude
        check(kotlin.math.abs(signedDegrees(actual - longitude)) < 0.15) {
            "$planet differs from golden value: $actual versus $longitude"
        }
    }
    check(kotlin.math.abs(signedDegrees(horoscope.ascendant.siderealLongitude - expectedAscendant)) < 0.15) {
        "Ascendant differs from golden value"
    }
}
