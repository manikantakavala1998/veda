package com.jyotishsutra.app.astrology

import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.tan

class HoroscopeCalculator(
    private val ephemeris: Ephemeris = OfflineEphemeris(),
) {
    fun calculate(
        birthDetails: BirthDetails,
        ayanamsa: Ayanamsa = Ayanamsa.LAHIRI,
    ): Horoscope {
        require(birthDetails.latitude in -90.0..90.0) { "Latitude must be between -90 and 90" }
        require(birthDetails.longitude in -180.0..180.0) { "Longitude must be between -180 and 180" }
        require(birthDetails.zoneOffsetMinutes in -18 * 60..18 * 60) { "Invalid UTC offset" }

        val jd = julianDay(birthDetails)
        val ayanamsaValue = ayanamsaDegrees(jd, ayanamsa)
        val tropical = ephemeris.tropicalLongitudes(jd)
        val later = ephemeris.tropicalLongitudes(jd + 1.0 / 24.0)
        val ascendantLongitude = siderealAscendant(jd, birthDetails, ayanamsaValue)
        val ascSign = floor(ascendantLongitude / 30.0).toInt()

        val planets = PlanetId.entries.map { id ->
            val tropicalLongitude = tropical.getValue(id)
            val siderealLongitude = normalizeDegrees(tropicalLongitude - ayanamsaValue)
            val sign = floor(siderealLongitude / 30.0).toInt()
            val nakshatraValue = nakshatraIndex(siderealLongitude)
            val hourlyMotion = signedDegrees(later.getValue(id) - tropicalLongitude)
            PlanetPosition(
                id = id,
                tropicalLongitude = tropicalLongitude,
                siderealLongitude = siderealLongitude,
                signIndex = sign,
                degreeInSign = siderealLongitude % 30.0,
                nakshatraIndex = nakshatraValue,
                pada = floor((siderealLongitude % NAKSHATRA_SPAN) / PADA_SPAN).toInt() + 1,
                house = ((sign - ascSign + 12) % 12) + 1,
                retrograde = when (id) {
                    PlanetId.SUN, PlanetId.MOON -> false
                    PlanetId.RAHU, PlanetId.KETU -> true
                    else -> hourlyMotion < 0.0
                },
            )
        }

        val sun = planets.first { it.id == PlanetId.SUN }.siderealLongitude
        val moon = planets.first { it.id == PlanetId.MOON }.siderealLongitude
        return Horoscope(
            birthDetails = birthDetails,
            julianDay = jd,
            ayanamsa = ayanamsa,
            ayanamsaDegrees = ayanamsaValue,
            ascendant = Ascendant(ascendantLongitude, ascSign, ascendantLongitude % 30.0),
            planets = planets,
            panchang = calculatePanchang(birthDetails.date, sun, moon),
            vimshottari = calculateVimshottari(birthDetails.date, moon),
        )
    }

    private fun siderealAscendant(
        julianDay: Double,
        details: BirthDetails,
        ayanamsa: Double,
    ): Double {
        val centuries = (julianDay - 2_451_545.0) / 36_525.0
        val gmst = 280.46061837 + 360.98564736629 * (julianDay - 2_451_545.0) +
            0.000387933 * centuries * centuries - centuries * centuries * centuries / 38_710_000.0
        val localSiderealTime = normalizeDegrees(gmst + details.longitude).toRadians()
        val obliquity = (23.439291 - 0.0130042 * centuries).toRadians()
        val latitude = details.latitude.coerceIn(-89.999, 89.999).toRadians()
        val tropicalAscendant = normalizeDegrees(
            atan2(
                -cos(localSiderealTime),
                sin(localSiderealTime) * cos(obliquity) + tan(latitude) * sin(obliquity),
            ).toDegrees() + 180.0,
        )
        return normalizeDegrees(tropicalAscendant - ayanamsa)
    }
}

private const val NAKSHATRA_SPAN = 360.0 / 27.0
private const val PADA_SPAN = NAKSHATRA_SPAN / 4.0

fun nakshatraIndex(siderealLongitude: Double): Int =
    floor(normalizeDegrees(siderealLongitude) / NAKSHATRA_SPAN).toInt().coerceIn(0, 26)

private val TITHI_NAMES = listOf(
    "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami", "Shashthi", "Saptami",
    "Ashtami", "Navami", "Dashami", "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Purnima",
    "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami", "Shashthi", "Saptami",
    "Ashtami", "Navami", "Dashami", "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Amavasya",
)

private val YOGA_NAMES = listOf(
    "Vishkambha", "Priti", "Ayushman", "Saubhagya", "Shobhana", "Atiganda", "Sukarma",
    "Dhriti", "Shula", "Ganda", "Vriddhi", "Dhruva", "Vyaghata", "Harshana", "Vajra",
    "Siddhi", "Vyatipata", "Variyana", "Parigha", "Shiva", "Siddha", "Sadhya", "Shubha",
    "Shukla", "Brahma", "Indra", "Vaidhriti",
)

private val REPEATING_KARANAS = listOf("Bava", "Balava", "Kaulava", "Taitila", "Garaja", "Vanija", "Vishti")

fun calculatePanchang(date: LocalDate, sunLongitude: Double, moonLongitude: Double): Panchang {
    val elongation = normalizeDegrees(moonLongitude - sunLongitude)
    val tithiIndex = floor(elongation / 12.0).toInt().coerceIn(0, 29)
    val yogaIndex = floor(normalizeDegrees(sunLongitude + moonLongitude) / NAKSHATRA_SPAN).toInt()
    val halfTithi = floor(elongation / 6.0).toInt().coerceIn(0, 59)
    val karana = when (halfTithi) {
        0 -> "Kimstughna"
        57 -> "Shakuni"
        58 -> "Chatushpada"
        59 -> "Naga"
        else -> REPEATING_KARANAS[(halfTithi - 1) % REPEATING_KARANAS.size]
    }
    return Panchang(
        vara = date.dayOfWeek.vedicName(),
        tithi = TITHI_NAMES[tithiIndex],
        paksha = if (tithiIndex < 15) "Shukla" else "Krishna",
        nakshatra = NAKSHATRA_NAMES[nakshatraIndex(moonLongitude)],
        yoga = YOGA_NAMES[yogaIndex],
        karana = karana,
    )
}

private fun DayOfWeek.vedicName(): String = when (this) {
    DayOfWeek.MONDAY -> "Somavara"
    DayOfWeek.TUESDAY -> "Mangalavara"
    DayOfWeek.WEDNESDAY -> "Budhavara"
    DayOfWeek.THURSDAY -> "Guruvara"
    DayOfWeek.FRIDAY -> "Shukravara"
    DayOfWeek.SATURDAY -> "Shanivara"
    DayOfWeek.SUNDAY -> "Ravivara"
}

internal val VIMSHOTTARI_LORDS = listOf(
    PlanetId.KETU to 7,
    PlanetId.VENUS to 20,
    PlanetId.SUN to 6,
    PlanetId.MOON to 10,
    PlanetId.MARS to 7,
    PlanetId.RAHU to 18,
    PlanetId.JUPITER to 16,
    PlanetId.SATURN to 19,
    PlanetId.MERCURY to 17,
)

fun calculateVimshottari(birthDate: LocalDate, moonLongitude: Double): List<DashaPeriod> {
    val nakshatra = nakshatraIndex(moonLongitude)
    val firstLordIndex = nakshatra % VIMSHOTTARI_LORDS.size
    val traversed = (normalizeDegrees(moonLongitude) % NAKSHATRA_SPAN) / NAKSHATRA_SPAN
    val firstYears = VIMSHOTTARI_LORDS[firstLordIndex].second
    var periodStart = birthDate.minusDays((firstYears * traversed * 365.2425).roundToLong())
    return buildList {
        repeat(10) { offset ->
            val (lord, years) = VIMSHOTTARI_LORDS[(firstLordIndex + offset) % VIMSHOTTARI_LORDS.size]
            val periodEnd = periodStart.plusDays((years * 365.2425).roundToLong())
            add(DashaPeriod(lord, periodStart, periodEnd, years))
            periodStart = periodEnd
        }
    }
}
