package com.jyotishsutra.app.astrology

import kotlin.math.floor

data class KpLords(
    val nakshatra: String,
    val starLord: PlanetId,
    val subLord: PlanetId,
    val subSubLord: PlanetId,
)

data class KpPlanetPosition(
    val planet: PlanetId,
    val siderealLongitude: Double,
    val lords: KpLords,
)

data class KpReport(val positions: List<KpPlanetPosition>)

/** KP stellar and sub-lord division using Vimshottari proportions. */
class KpCalculator {
    fun calculate(horoscope: Horoscope): KpReport = KpReport(
        positions = horoscope.planets.map {
            KpPlanetPosition(it.id, it.siderealLongitude, lordsAt(it.siderealLongitude))
        },
    )

    fun lordsAt(siderealLongitude: Double): KpLords {
        val longitude = normalizeDegrees(siderealLongitude)
        val nakshatraSpan = 360.0 / 27.0
        val nakshatra = floor(longitude / nakshatraSpan).toInt().coerceIn(0, 26)
        val starLordIndex = nakshatra % VIMSHOTTARI_LORDS.size
        val offset = longitude - nakshatra * nakshatraSpan
        val sub = findDivision(offset, nakshatraSpan, starLordIndex)
        val subSub = findDivision(sub.offsetInside, sub.span, sub.lordIndex)
        return KpLords(
            nakshatra = NAKSHATRA_NAMES[nakshatra],
            starLord = VIMSHOTTARI_LORDS[starLordIndex].first,
            subLord = VIMSHOTTARI_LORDS[sub.lordIndex].first,
            subSubLord = VIMSHOTTARI_LORDS[subSub.lordIndex].first,
        )
    }

    private data class Division(val lordIndex: Int, val span: Double, val offsetInside: Double)

    private fun findDivision(offset: Double, totalSpan: Double, startingLordIndex: Int): Division {
        var remaining = offset.coerceIn(0.0, totalSpan)
        repeat(VIMSHOTTARI_LORDS.size) { step ->
            val lordIndex = (startingLordIndex + step) % VIMSHOTTARI_LORDS.size
            val years = VIMSHOTTARI_LORDS[lordIndex].second
            val span = totalSpan * years / 120.0
            if (remaining <= span || step == VIMSHOTTARI_LORDS.lastIndex) {
                return Division(lordIndex, span, remaining.coerceAtMost(span))
            }
            remaining -= span
        }
        error("Unreachable Vimshottari division")
    }
}
