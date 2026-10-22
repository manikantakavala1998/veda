package com.jyotishsutra.app.palmistry

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Lightweight on-device feature extraction for the guided prototype.
 * A production release should replace this with a validated landmark/segmentation model.
 */
class PalmImageAnalyzer {
    fun analyze(bitmap: Bitmap, hand: PalmHand, role: HandRole): PalmObservation {
        val size = 192
        val scaled = Bitmap.createScaledBitmap(bitmap, size, size, true)
        val gray = DoubleArray(size * size)
        var luminance = 0.0
        var luminanceSquared = 0.0
        var skinPixels = 0
        var minX = size
        var minY = size
        var maxX = 0
        var maxY = 0

        for (y in 0 until size) for (x in 0 until size) {
            val color = scaled.getPixel(x, y)
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            val value = red * .299 + green * .587 + blue * .114
            gray[y * size + x] = value
            luminance += value
            luminanceSquared += value * value
            if (isLikelySkin(red, green, blue)) {
                skinPixels++
                minX = min(minX, x); minY = min(minY, y)
                maxX = max(maxX, x); maxY = max(maxY, y)
            }
        }

        var edgeEnergy = 0.0
        var edgeSamples = 0
        for (y in 1 until size - 1 step 2) for (x in 1 until size - 1 step 2) {
            val index = y * size + x
            edgeEnergy += abs(gray[index] - gray[index - 1]) + abs(gray[index] - gray[index - size])
            edgeSamples++
        }

        val pixelCount = (size * size).toDouble()
        val mean = luminance / pixelCount
        val contrast = sqrt(max(0.0, luminanceSquared / pixelCount - mean * mean))
        val sharpness = edgeEnergy / edgeSamples.coerceAtLeast(1)
        val coverage = skinPixels / pixelCount
        val qualityScore = (55 + contrast * .32 + sharpness * .45 - abs(142 - mean) * .12 + min(.35, coverage) * 35)
            .toInt().coerceIn(35, 98)
        val quality = ScanQuality(qualityScore, mean / 255, contrast / 128, sharpness / 128, coverage)

        val boxWidth = (maxX - minX).coerceAtLeast(1)
        val boxHeight = (maxY - minY).coerceAtLeast(1)
        val aspect = boxHeight.toDouble() / boxWidth
        val handType = classifyHand(aspect, contrast, coverage)

        fun feature(yStart: Double, yEnd: Double, bias: Double): LineFeature {
            val density = edgeDensity(gray, size, yStart, yEnd)
            val strength = (.48 + density * .9 + bias).coerceIn(.35, .95)
            return LineFeature(
                strength = strength,
                length = (.54 + coverage * .5 + bias / 2).coerceIn(.4, .94),
                curvature = (.34 + contrast / 180 + bias).coerceIn(.25, .9),
                branches = if (density > .24) 1 else 0,
                breaks = if (density < .11) 1 else 0,
            )
        }

        val lines = mapOf(
            PalmLine.HEART to feature(.37, .49, .07),
            PalmLine.HEAD to feature(.49, .62, .05),
            PalmLine.LIFE to feature(.52, .84, .09),
            PalmLine.FATE to feature(.46, .88, -.05),
            PalmLine.SUN to feature(.34, .76, -.08),
            PalmLine.MERCURY to feature(.40, .76, -.03),
        )
        val baseMount = (.48 + coverage * .38 + contrast / 300).coerceIn(.4, .9)
        val mounts = PalmMount.entries.associateWith { mount ->
            (baseMount + (mount.ordinal % 4 - 1.5) * .035).coerceIn(.38, .92)
        }
        val markings = buildSet {
            if (sharpness > 16) add(PalmMarking.FORK)
            if (contrast > 42) add(PalmMarking.CROSS)
            if (coverage > .28) add(PalmMarking.TRIANGLE)
            if (edgeEnergy / edgeSamples.coerceAtLeast(1) > 24) add(PalmMarking.STAR)
        }
        return PalmObservation(
            hand, role, quality, handType, lines, mounts, markings,
            thumbOpenness = (.48 + coverage * .45).coerceIn(.4, .9),
            fingerBalance = (.58 + (1 - abs(1.35 - aspect)) * .2).coerceIn(.4, .9),
        )
    }

    private fun isLikelySkin(red: Int, green: Int, blue: Int): Boolean =
        red > 75 && green > 30 && blue > 15 && red > green && red > blue &&
            max(red, max(green, blue)) - min(red, min(green, blue)) > 12

    private fun edgeDensity(gray: DoubleArray, size: Int, yStart: Double, yEnd: Double): Double {
        val from = (size * yStart).toInt().coerceIn(1, size - 2)
        val to = (size * yEnd).toInt().coerceIn(from + 1, size - 1)
        var hits = 0
        var count = 0
        for (y in from until to step 2) for (x in size / 5 until size * 4 / 5 step 2) {
            val index = y * size + x
            val edge = abs(gray[index] - gray[index - 1]) + abs(gray[index] - gray[index - size])
            if (edge > 35) hits++
            count++
        }
        return hits.toDouble() / count.coerceAtLeast(1)
    }

    private fun classifyHand(aspect: Double, contrast: Double, coverage: Double): HandType = when {
        coverage < .16 -> HandType.MIXED
        aspect > 1.75 -> HandType.PSYCHIC
        aspect > 1.5 && contrast > 48 -> HandType.PHILOSOPHIC
        aspect > 1.42 -> HandType.CONIC
        aspect < 1.08 -> HandType.ELEMENTARY
        aspect < 1.23 -> HandType.SQUARE
        contrast > 63 -> HandType.SPATULATE
        else -> HandType.MIXED
    }
}
