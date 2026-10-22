package com.jyotishsutra.app.astrology

import kotlin.math.floor

data class KootaScore(
    val name: String,
    val earned: Double,
    val maximum: Double,
    val detail: String,
)

data class CompatibilityResult(
    val personOne: Horoscope,
    val personTwo: Horoscope,
    val scores: List<KootaScore>,
) {
    val total: Double = scores.sumOf { it.earned }
    val verdict: String = when {
        total >= 28.0 -> "Strong traditional compatibility"
        total >= 21.0 -> "Favourable traditional compatibility"
        total >= 18.0 -> "Acceptable; review individual factors"
        else -> "Detailed consultation recommended"
    }
}

class CompatibilityCalculator(
    private val horoscopeCalculator: HoroscopeCalculator = HoroscopeCalculator(),
) {
    fun calculate(
        personOne: BirthDetails,
        personTwo: BirthDetails,
        ayanamsa: Ayanamsa = Ayanamsa.LAHIRI,
    ): CompatibilityResult {
        val first = horoscopeCalculator.calculate(personOne, ayanamsa)
        val second = horoscopeCalculator.calculate(personTwo, ayanamsa)
        val firstMoon = first.planets.first { it.id == PlanetId.MOON }
        val secondMoon = second.planets.first { it.id == PlanetId.MOON }
        val scores = listOf(
            varna(firstMoon.signIndex, secondMoon.signIndex),
            vashya(firstMoon.signIndex, secondMoon.signIndex),
            tara(firstMoon.nakshatraIndex, secondMoon.nakshatraIndex),
            yoni(firstMoon.nakshatraIndex, secondMoon.nakshatraIndex),
            grahaMaitri(firstMoon.signIndex, secondMoon.signIndex),
            gana(firstMoon.nakshatraIndex, secondMoon.nakshatraIndex),
            bhakoot(firstMoon.signIndex, secondMoon.signIndex),
            nadi(firstMoon.nakshatraIndex, secondMoon.nakshatraIndex),
        )
        return CompatibilityResult(first, second, scores)
    }

    private fun varna(firstSign: Int, secondSign: Int): KootaScore {
        val ranks = listOf(3, 2, 1, 4, 3, 2, 1, 4, 3, 2, 1, 4)
        val score = if (ranks[secondSign] >= ranks[firstSign]) 1.0 else 0.0
        return KootaScore("Varna", score, 1.0, "Spiritual disposition")
    }

    private fun vashya(firstSign: Int, secondSign: Int): KootaScore {
        val groups = listOf(0, 0, 1, 2, 3, 1, 1, 4, 0, 2, 1, 2)
        val a = groups[firstSign]
        val b = groups[secondSign]
        val score = when {
            a == b -> 2.0
            setOf(a, b) in VASHYA_FRIENDS -> 1.0
            setOf(a, b) in VASHYA_CONFLICTS -> 0.0
            else -> 0.5
        }
        return KootaScore("Vashya", score, 2.0, "Mutual influence")
    }

    private fun tara(firstNakshatra: Int, secondNakshatra: Int): KootaScore {
        fun favourable(from: Int, to: Int): Boolean {
            val count = ((to - from + 27) % 27) + 1
            return count % 9 in setOf(0, 2, 4, 6, 8)
        }
        val score = (if (favourable(firstNakshatra, secondNakshatra)) 1.5 else 0.0) +
            (if (favourable(secondNakshatra, firstNakshatra)) 1.5 else 0.0)
        return KootaScore("Tara", score, 3.0, "Birth-star harmony")
    }

    private fun yoni(firstNakshatra: Int, secondNakshatra: Int): KootaScore {
        val firstAnimal = YONI_ANIMALS[firstNakshatra]
        val secondAnimal = YONI_ANIMALS[secondNakshatra]
        val pair = setOf(firstAnimal, secondAnimal)
        val score = when {
            firstAnimal == secondAnimal -> 4.0
            pair in YONI_ENEMIES -> 0.0
            (firstAnimal + secondAnimal) % 3 == 0 -> 3.0
            (firstAnimal + secondAnimal) % 2 == 0 -> 2.0
            else -> 1.0
        }
        return KootaScore("Yoni", score, 4.0, "Instinctive compatibility")
    }

    private fun grahaMaitri(firstSign: Int, secondSign: Int): KootaScore {
        val firstLord = SIGN_LORDS[firstSign]
        val secondLord = SIGN_LORDS[secondSign]
        val firstRelation = relation(firstLord, secondLord)
        val secondRelation = relation(secondLord, firstLord)
        val score = when {
            firstLord == secondLord -> 5.0
            firstRelation == 1 && secondRelation == 1 -> 5.0
            firstRelation >= 0 && secondRelation >= 0 && firstRelation + secondRelation >= 1 -> 4.0
            firstRelation == 0 && secondRelation == 0 -> 3.0
            firstRelation == -1 && secondRelation == -1 -> 0.0
            else -> 1.0
        }
        return KootaScore("Graha Maitri", score, 5.0, "Lords of the Moon signs")
    }

    private fun gana(firstNakshatra: Int, secondNakshatra: Int): KootaScore {
        val a = GANA[firstNakshatra]
        val b = GANA[secondNakshatra]
        val score = when {
            a == b -> 6.0
            setOf(a, b) == setOf(0, 1) -> 5.0
            setOf(a, b) == setOf(0, 2) -> 1.0
            else -> 0.0
        }
        return KootaScore("Gana", score, 6.0, "Temperament")
    }

    private fun bhakoot(firstSign: Int, secondSign: Int): KootaScore {
        val forward = ((secondSign - firstSign + 12) % 12) + 1
        val reverse = ((firstSign - secondSign + 12) % 12) + 1
        val dosha = setOf(forward, reverse) in listOf(setOf(2, 12), setOf(5, 9), setOf(6, 8))
        return KootaScore("Bhakoot", if (dosha) 0.0 else 7.0, 7.0, "Moon-sign relationship")
    }

    private fun nadi(firstNakshatra: Int, secondNakshatra: Int): KootaScore {
        val score = if (firstNakshatra % 3 == secondNakshatra % 3) 0.0 else 8.0
        return KootaScore("Nadi", score, 8.0, "Constitutional compatibility")
    }

    private fun relation(from: PlanetId, to: PlanetId): Int {
        if (from == to) return 1
        if (to in FRIENDS[from].orEmpty()) return 1
        if (to in ENEMIES[from].orEmpty()) return -1
        return 0
    }

    private companion object {
        val VASHYA_FRIENDS = listOf(setOf(0, 1), setOf(1, 2), setOf(0, 2))
        val VASHYA_CONFLICTS = listOf(setOf(3, 0), setOf(3, 4), setOf(2, 4))

        // Horse, elephant, sheep, serpent, dog, cat, rat, cow, buffalo, tiger,
        // deer, monkey, mongoose and lion (indices 0..13).
        val YONI_ANIMALS = listOf(0, 1, 2, 3, 3, 4, 5, 2, 5, 6, 6, 7, 8, 9, 8, 9, 10, 10, 4, 11, 11, 12, 3, 0, 13, 7, 1)
        val YONI_ENEMIES = listOf(setOf(0, 8), setOf(1, 13), setOf(2, 11), setOf(3, 12), setOf(4, 10), setOf(5, 6), setOf(7, 9))

        // 0 Deva, 1 Manushya, 2 Rakshasa.
        val GANA = listOf(0, 1, 2, 1, 0, 1, 0, 0, 2, 2, 1, 1, 0, 2, 0, 2, 0, 2, 2, 1, 1, 0, 2, 2, 1, 1, 0)

        val SIGN_LORDS = listOf(
            PlanetId.MARS, PlanetId.VENUS, PlanetId.MERCURY, PlanetId.MOON,
            PlanetId.SUN, PlanetId.MERCURY, PlanetId.VENUS, PlanetId.MARS,
            PlanetId.JUPITER, PlanetId.SATURN, PlanetId.SATURN, PlanetId.JUPITER,
        )
        val FRIENDS = mapOf(
            PlanetId.SUN to setOf(PlanetId.MOON, PlanetId.MARS, PlanetId.JUPITER),
            PlanetId.MOON to setOf(PlanetId.SUN, PlanetId.MERCURY),
            PlanetId.MARS to setOf(PlanetId.SUN, PlanetId.MOON, PlanetId.JUPITER),
            PlanetId.MERCURY to setOf(PlanetId.SUN, PlanetId.VENUS),
            PlanetId.JUPITER to setOf(PlanetId.SUN, PlanetId.MOON, PlanetId.MARS),
            PlanetId.VENUS to setOf(PlanetId.MERCURY, PlanetId.SATURN),
            PlanetId.SATURN to setOf(PlanetId.MERCURY, PlanetId.VENUS),
        )
        val ENEMIES = mapOf(
            PlanetId.SUN to setOf(PlanetId.VENUS, PlanetId.SATURN),
            PlanetId.MOON to emptySet(),
            PlanetId.MARS to setOf(PlanetId.MERCURY),
            PlanetId.MERCURY to setOf(PlanetId.MOON),
            PlanetId.JUPITER to setOf(PlanetId.MERCURY, PlanetId.VENUS),
            PlanetId.VENUS to setOf(PlanetId.SUN, PlanetId.MOON),
            PlanetId.SATURN to setOf(PlanetId.SUN, PlanetId.MOON, PlanetId.MARS),
        )
    }
}
