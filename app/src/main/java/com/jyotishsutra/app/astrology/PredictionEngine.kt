package com.jyotishsutra.app.astrology

data class PredictionInsight(
    val category: String,
    val title: String,
    val interpretation: String,
    val evidence: List<String>,
)

data class YogaFinding(
    val name: String,
    val present: Boolean,
    val evidence: String,
)

data class GemstoneGuidance(
    val planet: PlanetId,
    val gemstone: String,
    val reason: String,
    val caution: String = "Traditional guidance only. Confirm suitability with a qualified astrologer before wearing.",
)

class PredictionEngine {
    fun generate(horoscope: Horoscope): List<PredictionInsight> {
        val moon = horoscope.position(PlanetId.MOON)
        val sun = horoscope.position(PlanetId.SUN)
        val tenthHouse = horoscope.planets.filter { it.house == 10 }
        val seventhHouse = horoscope.planets.filter { it.house == 7 }
        val wealthHouses = horoscope.planets.filter { it.house == 2 || it.house == 11 }
        val yogas = detectYogas(horoscope).filter { it.present }
        return listOf(
            PredictionInsight(
                category = "Personality",
                title = "${SIGN_NAMES[horoscope.ascendant.signIndex]} rising",
                interpretation = ASCENDANT_THEMES[horoscope.ascendant.signIndex],
                evidence = listOf("Ascendant ${formatLongitude(horoscope.ascendant.siderealLongitude)}"),
            ),
            PredictionInsight(
                category = "Mind",
                title = "Moon in ${SIGN_NAMES[moon.signIndex]}",
                interpretation = MOON_THEMES[moon.signIndex],
                evidence = listOf("${NAKSHATRA_NAMES[moon.nakshatraIndex]}, pada ${moon.pada}", "House ${moon.house}"),
            ),
            houseInsight("Work and vocation", 10, tenthHouse),
            houseInsight("Relationships", 7, seventhHouse),
            houseInsight("Resources and gains", 2, wealthHouses),
            PredictionInsight(
                category = "Purpose",
                title = "Sun in house ${sun.house}",
                interpretation = "Identity and confidence tend to develop through the themes of house ${sun.house}. " +
                    "Read this together with the Sun's sign, strength, aspects, and running dasha rather than in isolation.",
                evidence = listOf("Sun in ${SIGN_NAMES[sun.signIndex]} at ${formatLongitude(sun.siderealLongitude)}"),
            ),
        ) + if (yogas.isNotEmpty()) {
            listOf(
                PredictionInsight(
                    category = "Yogas",
                    title = yogas.joinToString { it.name },
                    interpretation = "These classical combinations are present by sign relationship. Their strength depends on dignity, aspects, combustion, divisional charts, and dasha activation.",
                    evidence = yogas.map { it.evidence },
                ),
            )
        } else {
            emptyList()
        }
    }

    fun detectYogas(horoscope: Horoscope): List<YogaFinding> {
        val sun = horoscope.position(PlanetId.SUN)
        val moon = horoscope.position(PlanetId.MOON)
        val mars = horoscope.position(PlanetId.MARS)
        val mercury = horoscope.position(PlanetId.MERCURY)
        val jupiter = horoscope.position(PlanetId.JUPITER)
        val moonToJupiter = ((jupiter.signIndex - moon.signIndex + 12) % 12) + 1
        return listOf(
            YogaFinding(
                "Budha-Aditya Yoga",
                sun.signIndex == mercury.signIndex,
                "Sun and Mercury share ${SIGN_NAMES[sun.signIndex]}",
            ),
            YogaFinding(
                "Chandra-Mangala Yoga",
                moon.signIndex == mars.signIndex,
                "Moon and Mars share ${SIGN_NAMES[moon.signIndex]}",
            ),
            YogaFinding(
                "Gajakesari Yoga",
                moonToJupiter in setOf(1, 4, 7, 10),
                "Jupiter is $moonToJupiter sign(s) from the Moon",
            ),
            YogaFinding(
                "Manglik consideration",
                mars.house in setOf(1, 2, 4, 7, 8, 12),
                "Mars occupies house ${mars.house}; cancellation rules must also be checked",
            ),
        )
    }

    fun gemstoneGuidance(horoscope: Horoscope): GemstoneGuidance {
        val ascendantLord = SIGN_LORDS[horoscope.ascendant.signIndex]
        val gemstone = GEMSTONES.getValue(ascendantLord)
        return GemstoneGuidance(
            planet = ascendantLord,
            gemstone = gemstone,
            reason = "${ascendantLord.displayName} rules the ${SIGN_NAMES[horoscope.ascendant.signIndex]} ascendant. " +
                "A full recommendation must also evaluate dignity, functional beneficence, dasha, and contraindications.",
        )
    }

    private fun Horoscope.position(id: PlanetId) = planets.first { it.id == id }

    private fun houseInsight(title: String, house: Int, occupants: List<PlanetPosition>): PredictionInsight {
        val names = occupants.joinToString { it.id.displayName }
        return PredictionInsight(
            category = title,
            title = if (occupants.isEmpty()) "House $house has no planets" else "$names influencing house $house",
            interpretation = if (occupants.isEmpty()) {
                "An empty house is not inactive. Its sign, lord, aspects, divisional support, and running periods carry the interpretation."
            } else {
                "The occupying planets make this life area prominent. Their dignity and relationships determine whether the expression is easy, demanding, or mixed."
            },
            evidence = occupants.map { "${it.id.displayName}: ${SIGN_NAMES[it.signIndex]} ${formatLongitude(it.siderealLongitude)}" },
        )
    }

    private fun formatLongitude(longitude: Double): String = String.format("%.2f°", normalizeDegrees(longitude))

    private companion object {
        val SIGN_LORDS = listOf(
            PlanetId.MARS, PlanetId.VENUS, PlanetId.MERCURY, PlanetId.MOON,
            PlanetId.SUN, PlanetId.MERCURY, PlanetId.VENUS, PlanetId.MARS,
            PlanetId.JUPITER, PlanetId.SATURN, PlanetId.SATURN, PlanetId.JUPITER,
        )
        val GEMSTONES = mapOf(
            PlanetId.SUN to "Ruby",
            PlanetId.MOON to "Natural pearl",
            PlanetId.MARS to "Red coral",
            PlanetId.MERCURY to "Emerald",
            PlanetId.JUPITER to "Yellow sapphire",
            PlanetId.VENUS to "Diamond or white sapphire",
            PlanetId.SATURN to "Blue sapphire",
        )
        val ASCENDANT_THEMES = listOf(
            "Initiative, directness, and independence are central developmental themes.",
            "Stability, patience, resources, and sensory appreciation shape the approach to life.",
            "Curiosity, language, adaptability, and exchange of ideas are emphasized.",
            "Care, belonging, protection, and emotional memory guide important choices.",
            "Creative visibility, dignity, leadership, and generous self-expression seek an outlet.",
            "Discernment, craft, service, analysis, and steady improvement organize experience.",
            "Balance, relationship, aesthetics, and fair exchange are recurring priorities.",
            "Depth, privacy, resilience, research, and transformation shape the life path.",
            "Meaning, learning, exploration, principles, and a broad horizon are emphasized.",
            "Structure, responsibility, endurance, and long-term achievement become central.",
            "Independent thinking, networks, systems, and collective concerns guide action.",
            "Sensitivity, imagination, compassion, and the search for integration are prominent.",
        )
        val MOON_THEMES = listOf(
            "Feelings move quickly toward action; emotional independence is important.",
            "The mind seeks steadiness, comfort, loyalty, and tangible security.",
            "Mental variety, conversation, and learning help emotions stay engaged.",
            "Emotional bonds, home, and familiar rhythms provide restoration.",
            "Warm recognition and creative expression nourish confidence.",
            "Order, usefulness, and practical improvement calm the mind.",
            "Harmony, companionship, and a balanced atmosphere support well-being.",
            "Feelings are intense and private; trust and emotional honesty matter deeply.",
            "Hope, movement, learning, and a sense of meaning renew enthusiasm.",
            "Emotional control, competence, and dependable progress create security.",
            "The mind values independence, ideas, friendship, and social contribution.",
            "Empathy and imagination are strong; clear boundaries preserve balance.",
        )
    }
}
