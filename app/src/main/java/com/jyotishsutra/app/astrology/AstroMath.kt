package com.jyotishsutra.app.astrology

import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.PI

internal fun normalizeDegrees(value: Double): Double {
    val result = value % 360.0
    return if (result < 0.0) result + 360.0 else result
}

internal fun signedDegrees(value: Double): Double {
    val normalized = normalizeDegrees(value)
    return if (normalized > 180.0) normalized - 360.0 else normalized
}

internal fun Double.toRadians(): Double = this * PI / 180.0
internal fun Double.toDegrees(): Double = this * 180.0 / PI

fun julianDay(details: BirthDetails): Double {
    val local = LocalDateTime.of(details.date, details.time)
    val offset = ZoneOffset.ofTotalSeconds(details.zoneOffsetMinutes * 60)
    return local.toEpochSecond(offset) / 86_400.0 + 2_440_587.5
}

internal fun ayanamsaDegrees(julianDay: Double, ayanamsa: Ayanamsa): Double {
    val tropicalYearsSinceJ2000 = (julianDay - 2_451_545.0) / 365.2425
    val precession = tropicalYearsSinceJ2000 * 0.013968878
    return when (ayanamsa) {
        Ayanamsa.LAHIRI -> 23.853055 + precession
        Ayanamsa.RAMAN -> 22.506667 + precession
        Ayanamsa.KRISHNAMURTI -> 23.856111 + precession
    }
}
