package com.jyotishsutra.app.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jyotishsutra.app.astrology.Horoscope
import com.jyotishsutra.app.astrology.NAKSHATRA_NAMES
import com.jyotishsutra.app.astrology.PlanetPosition
import com.jyotishsutra.app.astrology.PlanetId
import com.jyotishsutra.app.astrology.PredictionEngine
import com.jyotishsutra.app.astrology.SIGN_NAMES
import com.jyotishsutra.app.ui.theme.Aubergine
import com.jyotishsutra.app.ui.theme.MutedGold
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.floor

@Composable
fun HoroscopeScreen(
    horoscope: Horoscope,
    onCreateNew: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Rashi", "Planets", "Panchang", "Dasha", "Insights")
    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Rashi Kundali", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "${horoscope.birthDetails.name} · ${horoscope.birthDetails.placeName}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            OutlinedButton(onClick = onCreateNew) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Text("New", modifier = Modifier.padding(start = 4.dp))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tabs.forEachIndexed { index, title ->
                FilterChip(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    label = { Text(title) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        when (selectedTab) {
            0 -> ChartOverview(horoscope, Modifier.weight(1f))
            1 -> PlanetaryPositions(horoscope.planets, Modifier.weight(1f))
            2 -> PanchangContent(horoscope, Modifier.weight(1f))
            3 -> DashaContent(horoscope, Modifier.weight(1f))
            else -> InsightsContent(horoscope, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ChartOverview(horoscope: Horoscope, modifier: Modifier = Modifier) {
    val moon = horoscope.planets.first { it.id.displayName == "Moon" }
    var chartStyle by remember { mutableIntStateOf(0) }
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            listOf("North Indian", "South Indian").forEachIndexed { index, label ->
                FilterChip(
                    selected = chartStyle == index,
                    onClick = { chartStyle = index },
                    label = { Text(label) },
                )
            }
        }
        if (chartStyle == 0) {
            NorthIndianChart(
                ascendantSign = horoscope.ascendant.signIndex,
                planets = horoscope.planets,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            )
        } else {
            SouthIndianChart(
                ascendantSign = horoscope.ascendant.signIndex,
                planets = horoscope.planets,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SummaryChip("Moon", SIGN_NAMES[moon.signIndex], Modifier.weight(1f))
            SummaryChip("Lagna", SIGN_NAMES[horoscope.ascendant.signIndex], Modifier.weight(1f))
            SummaryChip("Nakshatra", "${NAKSHATRA_NAMES[moon.nakshatraIndex]} ${moon.pada}", Modifier.weight(1f))
        }
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            shape = RoundedCornerShape(18.dp),
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("Chart foundation", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${SIGN_NAMES[horoscope.ascendant.signIndex]} rises at " +
                        formatDegrees(horoscope.ascendant.degreeInSign) + ". " +
                        "The Moon occupies ${NAKSHATRA_NAMES[moon.nakshatraIndex]}, pada ${moon.pada}. " +
                        "Interpretations will be generated from traceable rules and can later be explained by Gemini.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        Text(
            "Ayanamsha: ${horoscope.ayanamsa.displayName} (${String.format("%.3f°", horoscope.ayanamsaDegrees)})",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 14.dp),
        )
    }
}

@Composable
fun NorthIndianChart(
    ascendantSign: Int,
    planets: List<PlanetPosition>,
    modifier: Modifier = Modifier,
) {
    val lineColor = MutedGold
    val textColor = MaterialTheme.colorScheme.onSurface
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFF9EC),
        tonalElevation = 2.dp,
    ) {
        Canvas(Modifier.fillMaxSize().padding(12.dp)) {
            val width = size.width
            val height = size.height
            val stroke = 2.dp.toPx()
            drawRect(lineColor, style = androidx.compose.ui.graphics.drawscope.Stroke(stroke))
            drawLine(lineColor, Offset(0f, 0f), Offset(width, height), stroke)
            drawLine(lineColor, Offset(width, 0f), Offset(0f, height), stroke)
            drawLine(lineColor, Offset(width / 2, 0f), Offset(width, height / 2), stroke)
            drawLine(lineColor, Offset(width, height / 2), Offset(width / 2, height), stroke)
            drawLine(lineColor, Offset(width / 2, height), Offset(0f, height / 2), stroke)
            drawLine(lineColor, Offset(0f, height / 2), Offset(width / 2, 0f), stroke)

            val positions = listOf(
                .50f to .22f, .25f to .10f, .10f to .25f, .22f to .50f,
                .10f to .75f, .25f to .90f, .50f to .78f, .75f to .90f,
                .90f to .75f, .78f to .50f, .90f to .25f, .75f to .10f,
            )
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = textColor.toArgb()
                textAlign = Paint.Align.CENTER
                textSize = 13.dp.toPx()
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            positions.forEachIndexed { index, position ->
                val house = index + 1
                val signIndex = (ascendantSign + index) % 12
                val occupants = planets.filter { it.house == house }
                val x = width * position.first
                val y = height * position.second
                val step = if (occupants.size > 3) 11.dp.toPx() else 15.dp.toPx()
                val firstY = y - ((occupants.size - 1).coerceAtLeast(0) * step / 2f)
                occupants.forEachIndexed { lineIndex, planet ->
                    paint.color = planetColor(planet.id).toArgb()
                    paint.textSize = (if (occupants.size > 3) 11.dp else 14.dp).toPx()
                    val label = "${planetGlyph(planet.id)} ${planet.id.shortName}${if (planet.retrograde) "℞" else ""}"
                    drawContext.canvas.nativeCanvas.drawText(label, x, firstY + lineIndex * step, paint)
                }
                paint.color = Color(0xFF765B42).toArgb()
                paint.textSize = 11.dp.toPx()
                val signY = if (occupants.isEmpty()) y else firstY + occupants.size * step
                drawContext.canvas.nativeCanvas.drawText("${ZODIAC_SYMBOLS[signIndex]} ${signIndex + 1}", x, signY, paint)
            }
        }
    }
}

@Composable
fun SouthIndianChart(
    ascendantSign: Int,
    planets: List<PlanetPosition>,
    modifier: Modifier = Modifier,
) {
    val lineColor = MutedGold
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFF9EC),
        tonalElevation = 2.dp,
    ) {
        Canvas(Modifier.fillMaxSize().padding(12.dp)) {
            val width = size.width
            val height = size.height
            val stroke = 2.dp.toPx()
            val quarterX = width / 4f
            val quarterY = height / 4f
            drawRect(lineColor, style = androidx.compose.ui.graphics.drawscope.Stroke(stroke))
            drawLine(lineColor, Offset(quarterX, 0f), Offset(quarterX, height), stroke)
            drawLine(lineColor, Offset(quarterX * 3f, 0f), Offset(quarterX * 3f, height), stroke)
            drawLine(lineColor, Offset(0f, quarterY), Offset(width, quarterY), stroke)
            drawLine(lineColor, Offset(0f, quarterY * 3f), Offset(width, quarterY * 3f), stroke)
            drawLine(lineColor, Offset(quarterX * 2f, 0f), Offset(quarterX * 2f, quarterY), stroke)
            drawLine(lineColor, Offset(quarterX * 2f, quarterY * 3f), Offset(quarterX * 2f, height), stroke)
            drawLine(lineColor, Offset(0f, quarterY * 2f), Offset(quarterX, quarterY * 2f), stroke)
            drawLine(lineColor, Offset(quarterX * 3f, quarterY * 2f), Offset(width, quarterY * 2f), stroke)
            drawRect(
                lineColor,
                topLeft = Offset(quarterX, quarterY),
                size = androidx.compose.ui.geometry.Size(quarterX * 2f, quarterY * 2f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(stroke),
            )

            val cells = listOf(
                Triple(11, .125f, .125f), Triple(0, .375f, .125f), Triple(1, .625f, .125f), Triple(2, .875f, .125f),
                Triple(10, .125f, .375f), Triple(3, .875f, .375f), Triple(9, .125f, .625f), Triple(4, .875f, .625f),
                Triple(8, .125f, .875f), Triple(7, .375f, .875f), Triple(6, .625f, .875f), Triple(5, .875f, .875f),
            )
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textAlign = Paint.Align.CENTER
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            cells.forEach { (signIndex, xRatio, yRatio) ->
                val occupants = planets.filter { it.signIndex == signIndex }
                val step = if (occupants.size > 3) 10.dp.toPx() else 13.dp.toPx()
                val centerY = height * yRatio
                val firstY = centerY - (occupants.size * step / 2f)
                occupants.forEachIndexed { index, planet ->
                    paint.color = planetColor(planet.id).toArgb()
                    paint.textSize = (if (occupants.size > 3) 10.dp else 13.dp).toPx()
                    val label = "${planetGlyph(planet.id)} ${planet.id.shortName}${if (planet.retrograde) "℞" else ""}"
                    drawContext.canvas.nativeCanvas.drawText(label, width * xRatio, firstY + index * step, paint)
                }
                paint.color = Color(0xFF765B42).toArgb()
                paint.textSize = 10.dp.toPx()
                val ascendant = if (signIndex == ascendantSign) " • Asc" else ""
                drawContext.canvas.nativeCanvas.drawText(
                    "${ZODIAC_SYMBOLS[signIndex]} ${SIGN_NAMES[signIndex].take(3)}$ascendant",
                    width * xRatio,
                    firstY + occupants.size * step + 12.dp.toPx(),
                    paint,
                )
            }
            paint.color = MutedGold.toArgb()
            paint.textSize = 28.dp.toPx()
            drawContext.canvas.nativeCanvas.drawText("ॐ", width / 2f, height / 2f + 9.dp.toPx(), paint)
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(Modifier.padding(10.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.labelLarge, maxLines = 1)
        }
    }
}

@Composable
private fun PlanetaryPositions(planets: List<PlanetPosition>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.padding(horizontal = 18.dp, vertical = 10.dp)) {
        item {
            Row(Modifier.fillMaxWidth().padding(12.dp)) {
                Text("Planet", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.1f))
                Text("Sign", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text("Degree", fontWeight = FontWeight.SemiBold)
            }
        }
        items(planets) { planet ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = CircleShape,
                    color = planetColor(planet.id),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(planetGlyph(planet.id), color = Color.White, style = MaterialTheme.typography.titleLarge)
                    }
                }
                Text(
                    "${planet.id.displayName}${if (planet.retrograde) " ℞" else ""}",
                    modifier = Modifier.padding(start = 9.dp).weight(1.1f),
                )
                Text(
                    "${ZODIAC_SYMBOLS[planet.signIndex]}  ${SIGN_NAMES[planet.signIndex]}",
                    color = planetColor(planet.id),
                    modifier = Modifier.weight(1f),
                )
                Text(formatDegrees(planet.degreeInSign))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        }
    }
}

@Composable
fun PanchangScreen(
    horoscope: Horoscope,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ScreenHeader("Panchang", onBack)
        PanchangContent(horoscope, Modifier.weight(1f))
    }
}

@Composable
private fun PanchangContent(horoscope: Horoscope, modifier: Modifier = Modifier) {
    val panchang = horoscope.panchang
    val values = listOf(
        "Vara" to panchang.vara,
        "Tithi" to "${panchang.paksha} ${panchang.tithi}",
        "Nakshatra" to panchang.nakshatra,
        "Yoga" to panchang.yoga,
        "Karana" to panchang.karana,
    )
    Column(modifier.verticalScroll(rememberScrollState()).padding(18.dp)) {
        Text(
            horoscope.birthDetails.date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            horoscope.birthDetails.placeName,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        values.forEach { (label, value) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Row(Modifier.fillMaxWidth().padding(18.dp)) {
                    Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                    Text(value, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun DashaScreen(
    horoscope: Horoscope,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ScreenHeader("Vimshottari Dasha", onBack)
        DashaContent(horoscope, Modifier.weight(1f))
    }
}

@Composable
private fun DashaContent(horoscope: Horoscope, modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    LazyColumn(modifier.padding(horizontal = 18.dp)) {
        item {
            Text(
                "Mahadasha timeline",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 12.dp),
            )
        }
        items(horoscope.vimshottari) { period ->
            val isCurrent = !today.isBefore(period.start) && today.isBefore(period.end)
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                ),
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCurrent) Aubergine else MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        Text(
                            period.lord.shortName,
                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        )
                    }
                    Column(Modifier.padding(start = 14.dp).weight(1f)) {
                        Text("${period.lord.displayName} Mahadasha", style = MaterialTheme.typography.titleMedium)
                        Text("${period.start} — ${period.end}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("${period.years}y")
                }
            }
        }
    }
}

@Composable
private fun InsightsContent(horoscope: Horoscope, modifier: Modifier = Modifier) {
    val engine = remember { PredictionEngine() }
    val insights = remember(horoscope) { engine.generate(horoscope) }
    val gemstone = remember(horoscope) { engine.gemstoneGuidance(horoscope) }
    LazyColumn(modifier.padding(horizontal = 18.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(18.dp),
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("Traditional gemstone review", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${gemstone.gemstone} · ${gemstone.planet.displayName}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Aubergine,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    Text(gemstone.reason, modifier = Modifier.padding(top = 6.dp))
                    Text(
                        gemstone.caution,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
        items(insights) { insight ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(insight.category.uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                    Text(insight.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 3.dp))
                    Text(insight.interpretation, modifier = Modifier.padding(top = 7.dp))
                    insight.evidence.forEach { evidence ->
                        Text(
                            "• $evidence",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }
        item {
            Text(
                "Interpretations are rule-based and intended for cultural reflection, not medical, legal, or financial decisions.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp),
            )
        }
    }
}

@Composable
fun ScreenHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
        }
        Text(title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(start = 4.dp))
    }
}

private fun formatDegrees(value: Double): String {
    val degrees = floor(value).toInt()
    val minutes = floor((value - degrees) * 60.0).toInt()
    return "%02d° %02d′".format(degrees, minutes)
}

private val ZODIAC_SYMBOLS = listOf("♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓")

private fun planetGlyph(id: PlanetId): String = when (id) {
    PlanetId.SUN -> "☉"
    PlanetId.MOON -> "☾"
    PlanetId.MARS -> "♂"
    PlanetId.MERCURY -> "☿"
    PlanetId.JUPITER -> "♃"
    PlanetId.VENUS -> "♀"
    PlanetId.SATURN -> "♄"
    PlanetId.RAHU -> "☊"
    PlanetId.KETU -> "☋"
}

private fun planetColor(id: PlanetId): Color = when (id) {
    PlanetId.SUN -> Color(0xFFF48223)
    PlanetId.MOON -> Color(0xFF70778B)
    PlanetId.MARS -> Color(0xFFC92E2E)
    PlanetId.MERCURY -> Color(0xFF348B42)
    PlanetId.JUPITER -> Color(0xFFD28B12)
    PlanetId.VENUS -> Color(0xFFED4F86)
    PlanetId.SATURN -> Color(0xFF633071)
    PlanetId.RAHU -> Color(0xFF3F4C68)
    PlanetId.KETU -> Color(0xFF934735)
}

private fun Color.toArgb(): Int = android.graphics.Color.argb(
    (alpha * 255).toInt(),
    (red * 255).toInt(),
    (green * 255).toInt(),
    (blue * 255).toInt(),
)
