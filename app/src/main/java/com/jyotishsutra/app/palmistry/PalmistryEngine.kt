package com.jyotishsutra.app.palmistry

import kotlin.math.roundToInt

enum class PalmHand(val displayName: String) { LEFT("Left hand"), RIGHT("Right hand") }
enum class HandRole(val displayName: String) { DOMINANT("Active tendencies"), NON_DOMINANT("Inherited tendencies") }

enum class HandType(val displayName: String) {
    ELEMENTARY("Elementary"),
    SQUARE("Square"),
    SPATULATE("Spatulate"),
    PHILOSOPHIC("Philosophic"),
    CONIC("Conic"),
    PSYCHIC("Psychic"),
    MIXED("Mixed / Air–Fire blend"),
}

enum class PalmLine(val displayName: String) {
    HEART("Heart Line"), HEAD("Head Line"), LIFE("Life Line"), FATE("Fate Line"),
    SUN("Sun / Apollo Line"), MERCURY("Mercury Line"),
}

enum class PalmMount(val displayName: String) {
    JUPITER("Jupiter"), SATURN("Saturn"), APOLLO("Apollo"), MERCURY("Mercury"),
    UPPER_MARS("Upper Mars"), LOWER_MARS("Lower Mars"), VENUS("Venus"),
    MOON("Moon"), PLAIN_OF_MARS("Plain of Mars"),
}

enum class PalmMarking(val displayName: String) {
    FORK("Fork"), CROSS("Cross"), SQUARE("Square"), TRIANGLE("Triangle"),
    ISLAND("Island"), BREAK("Break"), STAR("Star"),
}

data class ScanQuality(
    val score: Int,
    val brightness: Double,
    val contrast: Double,
    val sharpness: Double,
    val handCoverage: Double,
) {
    val label: String get() = when {
        score >= 85 -> "Excellent"
        score >= 70 -> "Good"
        score >= 55 -> "Usable"
        else -> "Retake recommended"
    }
}

data class LineFeature(
    val strength: Double,
    val length: Double,
    val curvature: Double,
    val branches: Int = 0,
    val breaks: Int = 0,
)

data class PalmObservation(
    val hand: PalmHand,
    val role: HandRole,
    val quality: ScanQuality,
    val handType: HandType,
    val lines: Map<PalmLine, LineFeature>,
    val mounts: Map<PalmMount, Double>,
    val markings: Set<PalmMarking>,
    val thumbOpenness: Double,
    val fingerBalance: Double,
)

data class RuleEvidence(
    val ruleId: String,
    val feature: String,
    val measuredValue: String,
)

data class PalmInsight(
    val title: String,
    val summary: String,
    val confidence: Int,
    val evidence: List<RuleEvidence>,
)

data class PalmReading(
    val observation: PalmObservation,
    val lineInsights: Map<PalmLine, PalmInsight>,
    val mountInsights: Map<PalmMount, PalmInsight>,
    val overview: List<PalmInsight>,
    val safetyNote: String = "Traditional cultural interpretation for personal reflection — not medical, legal or financial advice.",
)

/**
 * A deterministic, auditable Cheiro-inspired interpretation layer.
 * It does not make medical diagnoses, predict lifespan, or guarantee future events.
 */
class CheiroPalmistryEngine {
    fun interpret(observation: PalmObservation): PalmReading {
        val lineInsights = observation.lines.mapValues { (line, feature) -> lineInsight(line, feature, observation.quality.score) }
        val mountInsights = observation.mounts.mapValues { (mount, prominence) -> mountInsight(mount, prominence, observation.quality.score) }
        val overview = buildList {
            add(handTypeInsight(observation))
            add(characterInsight(observation, lineInsights, mountInsights))
            add(workInsight(observation, lineInsights, mountInsights))
            add(relationshipInsight(observation, lineInsights, mountInsights))
            add(balanceInsight(observation, lineInsights, mountInsights))
        }
        return PalmReading(observation, lineInsights, mountInsights, overview)
    }

    private fun lineInsight(line: PalmLine, feature: LineFeature, scanQuality: Int): PalmInsight {
        val form = when {
            feature.strength >= .78 && feature.length >= .72 -> "deep and well defined"
            feature.strength >= .62 -> "clear and balanced"
            feature.length >= .72 -> "long and finely marked"
            else -> "fine or developing"
        }
        val curve = when {
            feature.curvature >= .68 -> "with a pronounced curve"
            feature.curvature >= .42 -> "with a gentle curve"
            else -> "with a straighter course"
        }
        val modifiers = buildList {
            if (feature.branches > 0) add("${feature.branches} visible branch${if (feature.branches == 1) "" else "es"}")
            if (feature.breaks > 0) add("${feature.breaks} possible interruption${if (feature.breaks == 1) "" else "s"}")
        }
        val meaning = when (line) {
            PalmLine.HEART -> "Traditionally associated with emotional expression and relationship style"
            PalmLine.HEAD -> "Traditionally associated with focus, reasoning and imagination"
            PalmLine.LIFE -> "Traditionally associated with vitality and approach to change; it does not measure lifespan"
            PalmLine.FATE -> "Traditionally associated with direction, responsibility and changing priorities"
            PalmLine.SUN -> "Traditionally associated with creative expression and recognition"
            PalmLine.MERCURY -> "Traditionally associated with communication and practical adaptability"
        }
        return PalmInsight(
            title = line.displayName,
            summary = "$meaning. The detected line is $form $curve${if (modifiers.isEmpty()) "." else ", with ${modifiers.joinToString(" and ")}."}",
            confidence = combinedConfidence(scanQuality, feature.strength),
            evidence = listOf(
                RuleEvidence("LINE-${line.name}-DEPTH", "Line strength", percent(feature.strength)),
                RuleEvidence("LINE-${line.name}-LENGTH", "Relative length", percent(feature.length)),
                RuleEvidence("LINE-${line.name}-CURVE", "Curvature", percent(feature.curvature)),
            ),
        )
    }

    private fun mountInsight(mount: PalmMount, prominence: Double, scanQuality: Int): PalmInsight {
        val level = when {
            prominence >= .78 -> "pronounced"
            prominence >= .58 -> "balanced"
            else -> "subtle"
        }
        val theme = when (mount) {
            PalmMount.JUPITER -> "ambition, confidence and responsibility"
            PalmMount.SATURN -> "patience, reflection and discipline"
            PalmMount.APOLLO -> "creative expression and appreciation"
            PalmMount.MERCURY -> "communication and practical skill"
            PalmMount.UPPER_MARS -> "composure under pressure"
            PalmMount.LOWER_MARS -> "initiative and steady courage"
            PalmMount.VENUS -> "warmth, affection and engagement with life"
            PalmMount.MOON -> "imagination, empathy and receptivity"
            PalmMount.PLAIN_OF_MARS -> "balance between assertion and restraint"
        }
        return PalmInsight(
            mount.displayName,
            "The ${mount.displayName} area appears $level. In traditional palmistry this is considered alongside other features when reflecting on $theme.",
            combinedConfidence(scanQuality, prominence),
            listOf(RuleEvidence("MOUNT-${mount.name}", "Relative prominence", percent(prominence))),
        )
    }

    private fun handTypeInsight(observation: PalmObservation) = PalmInsight(
        title = "Hand type",
        summary = "The proportions most closely match ${observation.handType.displayName}. This is used as the base temperament layer before individual lines and mounts are considered.",
        confidence = combinedConfidence(observation.quality.score, observation.fingerBalance),
        evidence = listOf(
            RuleEvidence("HAND-TYPE", "Proportion class", observation.handType.displayName),
            RuleEvidence("FINGER-BALANCE", "Finger balance", percent(observation.fingerBalance)),
        ),
    )

    private fun characterInsight(
        observation: PalmObservation,
        lines: Map<PalmLine, PalmInsight>,
        mounts: Map<PalmMount, PalmInsight>,
    ) = PalmInsight(
        "Character",
        if ((observation.mounts[PalmMount.JUPITER] ?: 0.0) >= .7) {
            "A self-directed, warm style with a preference for meaningful responsibility. Reflection and initiative work best when kept in balance."
        } else {
            "A measured, observant style that tends to build confidence through experience and steady participation."
        },
        aggregateConfidence(observation.quality.score, lines.getValue(PalmLine.HEAD).confidence, mounts.getValue(PalmMount.JUPITER).confidence),
        listOf(
            RuleEvidence("OVERVIEW-CHARACTER", "Head line + Jupiter mount", "Combined"),
            RuleEvidence("THUMB-OPENNESS", "Thumb openness", percent(observation.thumbOpenness)),
        ),
    )

    private fun workInsight(
        observation: PalmObservation,
        lines: Map<PalmLine, PalmInsight>,
        mounts: Map<PalmMount, PalmInsight>,
    ) = PalmInsight(
        "Work & direction",
        "The Fate, Head and Apollo indicators are read together as a tendency toward purposeful work, adaptable planning and gradual recognition rather than a guaranteed career event.",
        aggregateConfidence(observation.quality.score, lines.getValue(PalmLine.FATE).confidence, mounts.getValue(PalmMount.APOLLO).confidence),
        listOf(RuleEvidence("OVERVIEW-WORK", "Fate line + Head line + Apollo mount", "Combined")),
    )

    private fun relationshipInsight(
        observation: PalmObservation,
        lines: Map<PalmLine, PalmInsight>,
        mounts: Map<PalmMount, PalmInsight>,
    ) = PalmInsight(
        "Relationships",
        "The Heart line and Venus area suggest a style of expressing care and connection. This describes tendencies, not a promised relationship outcome or date.",
        aggregateConfidence(observation.quality.score, lines.getValue(PalmLine.HEART).confidence, mounts.getValue(PalmMount.VENUS).confidence),
        listOf(RuleEvidence("OVERVIEW-RELATIONSHIP", "Heart line + Venus mount", "Combined")),
    )

    private fun balanceInsight(
        observation: PalmObservation,
        lines: Map<PalmLine, PalmInsight>,
        mounts: Map<PalmMount, PalmInsight>,
    ) = PalmInsight(
        "Personal balance",
        "Life-line clarity, the Mars areas and thumb balance are interpreted as a traditional reflection on pace, resilience and self-regulation—not as a health assessment.",
        aggregateConfidence(observation.quality.score, lines.getValue(PalmLine.LIFE).confidence, mounts.getValue(PalmMount.PLAIN_OF_MARS).confidence),
        listOf(RuleEvidence("OVERVIEW-BALANCE", "Life line + Mars + thumb", "Combined")),
    )

    private fun combinedConfidence(scanQuality: Int, feature: Double): Int =
        ((scanQuality * .58) + (feature.coerceIn(0.0, 1.0) * 100 * .42)).roundToInt().coerceIn(0, 100)

    private fun aggregateConfidence(scanQuality: Int, vararg values: Int): Int =
        ((scanQuality + values.sum()) / (values.size + 1)).coerceIn(0, 100)

    private fun percent(value: Double) = "${(value.coerceIn(0.0, 1.0) * 100).roundToInt()}%"

    companion object {
        fun sampleObservation(
            hand: PalmHand = PalmHand.RIGHT,
            role: HandRole = HandRole.DOMINANT,
        ) = PalmObservation(
            hand = hand,
            role = role,
            quality = ScanQuality(92, .61, .72, .84, .68),
            handType = HandType.MIXED,
            lines = mapOf(
                PalmLine.HEART to LineFeature(.82, .78, .72, branches = 1),
                PalmLine.HEAD to LineFeature(.88, .9, .55, branches = 1),
                PalmLine.LIFE to LineFeature(.86, .88, .81),
                PalmLine.FATE to LineFeature(.66, .63, .28, breaks = 1),
                PalmLine.SUN to LineFeature(.61, .58, .22),
                PalmLine.MERCURY to LineFeature(.73, .64, .34),
            ),
            mounts = mapOf(
                PalmMount.JUPITER to .78, PalmMount.SATURN to .67, PalmMount.APOLLO to .76,
                PalmMount.MERCURY to .70, PalmMount.UPPER_MARS to .74, PalmMount.LOWER_MARS to .71,
                PalmMount.VENUS to .82, PalmMount.MOON to .77, PalmMount.PLAIN_OF_MARS to .69,
            ),
            markings = setOf(PalmMarking.FORK, PalmMarking.CROSS, PalmMarking.TRIANGLE, PalmMarking.SQUARE),
            thumbOpenness = .72,
            fingerBalance = .79,
        )
    }
}
