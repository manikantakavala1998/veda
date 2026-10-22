package com.jyotishsutra.app.astrology

import java.time.LocalDate
import java.time.LocalTime

data class BirthDetails(
    val name: String,
    val date: LocalDate,
    val time: LocalTime,
    val zoneOffsetMinutes: Int,
    val latitude: Double,
    val longitude: Double,
    val placeName: String = "",
)

enum class Ayanamsa(val displayName: String) {
    LAHIRI("Lahiri / Chitrapaksha"),
    RAMAN("B. V. Raman"),
    KRISHNAMURTI("Krishnamurti"),
}

enum class PlanetId(val displayName: String, val shortName: String) {
    SUN("Sun", "Su"),
    MOON("Moon", "Mo"),
    MARS("Mars", "Ma"),
    MERCURY("Mercury", "Me"),
    JUPITER("Jupiter", "Ju"),
    VENUS("Venus", "Ve"),
    SATURN("Saturn", "Sa"),
    RAHU("Rahu", "Ra"),
    KETU("Ketu", "Ke"),
}

data class PlanetPosition(
    val id: PlanetId,
    val tropicalLongitude: Double,
    val siderealLongitude: Double,
    val signIndex: Int,
    val degreeInSign: Double,
    val nakshatraIndex: Int,
    val pada: Int,
    val house: Int,
    val retrograde: Boolean,
)

data class Ascendant(
    val siderealLongitude: Double,
    val signIndex: Int,
    val degreeInSign: Double,
)

data class Panchang(
    val vara: String,
    val tithi: String,
    val paksha: String,
    val nakshatra: String,
    val yoga: String,
    val karana: String,
)

data class DashaPeriod(
    val lord: PlanetId,
    val start: LocalDate,
    val end: LocalDate,
    val years: Int,
)

data class Horoscope(
    val birthDetails: BirthDetails,
    val julianDay: Double,
    val ayanamsa: Ayanamsa,
    val ayanamsaDegrees: Double,
    val ascendant: Ascendant,
    val planets: List<PlanetPosition>,
    val panchang: Panchang,
    val vimshottari: List<DashaPeriod>,
)

val SIGN_NAMES = listOf(
    "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
    "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces",
)

val NAKSHATRA_NAMES = listOf(
    "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra",
    "Punarvasu", "Pushya", "Ashlesha", "Magha", "Purva Phalguni",
    "Uttara Phalguni", "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha",
    "Jyeshtha", "Mula", "Purva Ashadha", "Uttara Ashadha", "Shravana",
    "Dhanishta", "Shatabhisha", "Purva Bhadrapada", "Uttara Bhadrapada", "Revati",
)
