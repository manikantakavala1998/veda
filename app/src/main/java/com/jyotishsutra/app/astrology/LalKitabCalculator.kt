package com.jyotishsutra.app.astrology

data class LalKitabHouse(
    val house: Int,
    val planets: List<PlanetId>,
    val lifeArea: String,
)

data class TraditionalRemedy(
    val planet: PlanetId,
    val practice: String,
    val note: String,
)

data class LalKitabReport(
    val houses: List<LalKitabHouse>,
    val remedies: List<TraditionalRemedy>,
)

/**
 * Creates the Lal Kitab fixed-house work chart from calculated bhava placements.
 * Predictive benefic/malefic rules are deliberately not inferred here; they will be added only
 * from a reviewed and citable rule corpus because those rules differ across editions and schools.
 */
class LalKitabCalculator {
    fun calculate(horoscope: Horoscope): LalKitabReport {
        val houses = (1..12).map { house ->
            LalKitabHouse(
                house = house,
                planets = horoscope.planets.filter { it.house == house }.map { it.id },
                lifeArea = HOUSE_AREAS[house - 1],
            )
        }
        val occupiedPlanets = horoscope.planets.map { it.id }.distinct()
        return LalKitabReport(
            houses = houses,
            remedies = occupiedPlanets.map { planet ->
                TraditionalRemedy(
                    planet = planet,
                    practice = SAFE_PRACTICES.getValue(planet),
                    note = "A non-harmful traditional practice for reflection; not a substitute for professional advice.",
                )
            },
        )
    }

    private companion object {
        val HOUSE_AREAS = listOf(
            "Self, vitality and personal direction",
            "Family, speech and accumulated resources",
            "Effort, communication and siblings",
            "Home, foundations and emotional security",
            "Learning, creativity and children",
            "Service, obstacles and daily discipline",
            "Partnership and public dealings",
            "Change, inheritance and hidden matters",
            "Principles, teachers and long journeys",
            "Work, responsibility and public standing",
            "Gains, networks and aspirations",
            "Rest, expenditure and release",
        )
        val SAFE_PRACTICES = mapOf(
            PlanetId.SUN to "Offer practical support to elders and act with integrity in positions of responsibility.",
            PlanetId.MOON to "Support caregivers, keep a steady sleep routine, and practice calm reflection.",
            PlanetId.MARS to "Channel excess energy into disciplined exercise and constructive service.",
            PlanetId.MERCURY to "Practice careful speech, continuous learning, and transparent record keeping.",
            PlanetId.JUPITER to "Support education, share knowledge responsibly, and give within your means.",
            PlanetId.VENUS to "Cultivate cleanliness, gratitude, art, and respectful relationships.",
            PlanetId.SATURN to "Serve people doing difficult work and build patient, consistent routines.",
            PlanetId.RAHU to "Avoid intoxicants and manipulative shortcuts; verify information before acting.",
            PlanetId.KETU to "Practice humility, quiet contemplation, and care for animals when practical.",
        )
    }
}
