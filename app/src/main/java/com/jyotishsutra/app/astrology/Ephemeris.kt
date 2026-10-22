package com.jyotishsutra.app.astrology

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A deterministic, offline, low-precision ephemeris suitable for an MVP and UI validation.
 * It intentionally sits behind an interface so a licensed Swiss Ephemeris or JPL adapter can
 * replace it before professional use without changing the rest of the application.
 */
interface Ephemeris {
    fun tropicalLongitudes(julianDay: Double): Map<PlanetId, Double>
}

class OfflineEphemeris : Ephemeris {
    private data class Elements(
        val node: Double,
        val inclination: Double,
        val perihelion: Double,
        val semiMajorAxis: Double,
        val eccentricity: Double,
        val meanAnomaly: Double,
    )

    private data class Vector3(val x: Double, val y: Double, val z: Double)
    private data class SolarState(val longitude: Double, val meanAnomaly: Double, val vector: Vector3)

    override fun tropicalLongitudes(julianDay: Double): Map<PlanetId, Double> {
        val d = julianDay - 2_451_543.5
        val sun = sunState(d)
        val positions = linkedMapOf<PlanetId, Double>()
        positions[PlanetId.SUN] = sun.longitude
        positions[PlanetId.MOON] = moonLongitude(d, sun)

        val mercury = Elements(
            48.3313 + 3.24587e-5 * d, 7.0047 + 5.00e-8 * d,
            29.1241 + 1.01444e-5 * d, 0.387098, 0.205635 + 5.59e-10 * d,
            168.6562 + 4.0923344368 * d,
        )
        val venus = Elements(
            76.6799 + 2.46590e-5 * d, 3.3946 + 2.75e-8 * d,
            54.8910 + 1.38374e-5 * d, 0.723330, 0.006773 - 1.302e-9 * d,
            48.0052 + 1.6021302244 * d,
        )
        val mars = Elements(
            49.5574 + 2.11081e-5 * d, 1.8497 - 1.78e-8 * d,
            286.5016 + 2.92961e-5 * d, 1.523688, 0.093405 + 2.516e-9 * d,
            18.6021 + 0.5240207766 * d,
        )
        val jupiter = Elements(
            100.4542 + 2.76854e-5 * d, 1.3030 - 1.557e-7 * d,
            273.8777 + 1.64505e-5 * d, 5.20256, 0.048498 + 4.469e-9 * d,
            19.8950 + 0.0830853001 * d,
        )
        val saturn = Elements(
            113.6634 + 2.38980e-5 * d, 2.4886 - 1.081e-7 * d,
            339.3939 + 2.97661e-5 * d, 9.55475, 0.055546 - 9.499e-9 * d,
            316.9670 + 0.0334442282 * d,
        )

        positions[PlanetId.MERCURY] = geocentricLongitude(mercury, sun.vector)
        positions[PlanetId.VENUS] = geocentricLongitude(venus, sun.vector)
        positions[PlanetId.MARS] = geocentricLongitude(mars, sun.vector)
        positions[PlanetId.JUPITER] = geocentricLongitude(jupiter, sun.vector)
        positions[PlanetId.SATURN] = geocentricLongitude(saturn, sun.vector)

        val meanNode = normalizeDegrees(125.1228 - 0.0529538083 * d)
        positions[PlanetId.RAHU] = meanNode
        positions[PlanetId.KETU] = normalizeDegrees(meanNode + 180.0)
        return positions
    }

    private fun sunState(d: Double): SolarState {
        val perihelion = 282.9404 + 4.70935e-5 * d
        val eccentricity = 0.016709 - 1.151e-9 * d
        val meanAnomaly = normalizeDegrees(356.0470 + 0.9856002585 * d)
        val eccentricAnomaly = eccentricAnomaly(meanAnomaly, eccentricity)
        val x = cos(eccentricAnomaly) - eccentricity
        val y = sqrt(1.0 - eccentricity * eccentricity) * sin(eccentricAnomaly)
        val trueAnomaly = atan2(y, x).toDegrees()
        val radius = sqrt(x * x + y * y)
        val longitude = normalizeDegrees(trueAnomaly + perihelion)
        return SolarState(
            longitude = longitude,
            meanAnomaly = meanAnomaly,
            vector = Vector3(
                radius * cos(longitude.toRadians()),
                radius * sin(longitude.toRadians()),
                0.0,
            ),
        )
    }

    private fun moonLongitude(d: Double, sun: SolarState): Double {
        val node = normalizeDegrees(125.1228 - 0.0529538083 * d)
        val inclination = 5.1454
        val perihelion = normalizeDegrees(318.0634 + 0.1643573223 * d)
        val eccentricity = 0.054900
        val meanAnomaly = normalizeDegrees(115.3654 + 13.0649929509 * d)
        val eccentricAnomaly = eccentricAnomaly(meanAnomaly, eccentricity)
        val x = 60.2666 * (cos(eccentricAnomaly) - eccentricity)
        val y = 60.2666 * sqrt(1.0 - eccentricity * eccentricity) * sin(eccentricAnomaly)
        val trueAnomaly = atan2(y, x).toDegrees()
        val radius = sqrt(x * x + y * y)
        val orbitalLongitude = normalizeDegrees(trueAnomaly + perihelion)

        val xh = radius * (
            cos(node.toRadians()) * cos(orbitalLongitude.toRadians()) -
                sin(node.toRadians()) * sin(orbitalLongitude.toRadians()) * cos(inclination.toRadians())
            )
        val yh = radius * (
            sin(node.toRadians()) * cos(orbitalLongitude.toRadians()) +
                cos(node.toRadians()) * sin(orbitalLongitude.toRadians()) * cos(inclination.toRadians())
            )
        var longitude = atan2(yh, xh).toDegrees()

        val moonMeanLongitude = normalizeDegrees(node + perihelion + meanAnomaly)
        val elongation = normalizeDegrees(moonMeanLongitude - sun.longitude)
        val argumentLatitude = normalizeDegrees(moonMeanLongitude - node)
        fun sind(value: Double) = sin(value.toRadians())
        longitude += -1.274 * sind(meanAnomaly - 2.0 * elongation)
        longitude += 0.658 * sind(2.0 * elongation)
        longitude += -0.186 * sind(sun.meanAnomaly)
        longitude += -0.059 * sind(2.0 * meanAnomaly - 2.0 * elongation)
        longitude += -0.057 * sind(meanAnomaly - 2.0 * elongation + sun.meanAnomaly)
        longitude += 0.053 * sind(meanAnomaly + 2.0 * elongation)
        longitude += 0.046 * sind(2.0 * elongation - sun.meanAnomaly)
        longitude += 0.041 * sind(meanAnomaly - sun.meanAnomaly)
        longitude += -0.035 * sind(elongation)
        longitude += -0.031 * sind(meanAnomaly + sun.meanAnomaly)
        longitude += -0.015 * sind(2.0 * argumentLatitude - 2.0 * elongation)
        longitude += 0.011 * sind(meanAnomaly - 4.0 * elongation)
        return normalizeDegrees(longitude)
    }

    private fun geocentricLongitude(elements: Elements, sunVector: Vector3): Double {
        val eccentricAnomaly = eccentricAnomaly(elements.meanAnomaly, elements.eccentricity)
        val xv = elements.semiMajorAxis * (cos(eccentricAnomaly) - elements.eccentricity)
        val yv = elements.semiMajorAxis * sqrt(1.0 - elements.eccentricity * elements.eccentricity) *
            sin(eccentricAnomaly)
        val trueAnomaly = atan2(yv, xv)
        val radius = sqrt(xv * xv + yv * yv)
        val longitude = trueAnomaly + elements.perihelion.toRadians()
        val node = elements.node.toRadians()
        val inclination = elements.inclination.toRadians()
        val xh = radius * (cos(node) * cos(longitude) - sin(node) * sin(longitude) * cos(inclination))
        val yh = radius * (sin(node) * cos(longitude) + cos(node) * sin(longitude) * cos(inclination))
        return normalizeDegrees(atan2(yh + sunVector.y, xh + sunVector.x).toDegrees())
    }

    private fun eccentricAnomaly(meanAnomalyDegrees: Double, eccentricity: Double): Double {
        val meanAnomaly = normalizeDegrees(meanAnomalyDegrees).toRadians()
        var estimate = meanAnomaly + eccentricity * sin(meanAnomaly) * (1.0 + eccentricity * cos(meanAnomaly))
        repeat(5) {
            estimate -= (estimate - eccentricity * sin(estimate) - meanAnomaly) /
                (1.0 - eccentricity * cos(estimate))
        }
        return estimate
    }
}
