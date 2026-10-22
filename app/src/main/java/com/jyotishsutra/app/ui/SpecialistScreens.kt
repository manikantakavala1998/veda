package com.jyotishsutra.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jyotishsutra.app.astrology.Horoscope
import com.jyotishsutra.app.astrology.KpCalculator
import com.jyotishsutra.app.astrology.LalKitabCalculator
import com.jyotishsutra.app.astrology.PredictionEngine
import com.jyotishsutra.app.astrology.SIGN_NAMES
import com.jyotishsutra.app.ui.theme.Aubergine

@Composable
fun KpScreen(
    horoscope: Horoscope,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val report = remember(horoscope) { KpCalculator().calculate(horoscope) }
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ScreenHeader("KP Astrology", onBack)
        LazyColumn(Modifier.padding(horizontal = 18.dp)) {
            item {
                Text(
                    "Stellar significators",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Text(
                    "Planetary star, sub, and sub-sub lords use Vimshottari proportions with ${horoscope.ayanamsa.displayName}.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 14.dp),
                )
            }
            items(report.positions) { position ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(Modifier.padding(15.dp)) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(position.planet.displayName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            Text(SIGN_NAMES[(position.siderealLongitude / 30.0).toInt()])
                        }
                        Text(
                            position.lords.nakshatra,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 5.dp),
                        )
                        Text(
                            "Star ${position.lords.starLord.displayName}  ·  " +
                                "Sub ${position.lords.subLord.displayName}  ·  " +
                                "Sub-sub ${position.lords.subSubLord.displayName}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 3.dp),
                        )
                    }
                }
            }
            item {
                Text(
                    "Cusp and ruling-planet judgment is the next KP milestone; this screen currently validates the stellar division foundation.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
        }
    }
}

@Composable
fun LalKitabScreen(
    horoscope: Horoscope,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val report = remember(horoscope) { LalKitabCalculator().calculate(horoscope) }
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ScreenHeader("Lal Kitab", onBack)
        LazyColumn(Modifier.padding(horizontal = 18.dp)) {
            item {
                Text("Fixed-house work chart", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Planet placements are ready. Edition-specific prediction rules will only be enabled after the rule corpus is reviewed.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 14.dp),
                )
            }
            items(report.houses) { house ->
                Row(Modifier.fillMaxWidth().padding(vertical = 11.dp)) {
                    Text("${house.house}", color = Aubergine, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(house.lifeArea, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            if (house.planets.isEmpty()) "No planets" else house.planets.joinToString { it.displayName },
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
            }
        }
    }
}

@Composable
fun RemediesScreen(
    horoscope: Horoscope,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lalKitab = remember(horoscope) { LalKitabCalculator().calculate(horoscope) }
    val gemstone = remember(horoscope) { PredictionEngine().gemstoneGuidance(horoscope) }
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ScreenHeader("Remedies", onBack)
        LazyColumn(Modifier.padding(horizontal = 18.dp)) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Gemstone for professional review", style = MaterialTheme.typography.titleMedium)
                        Text(gemstone.gemstone, style = MaterialTheme.typography.headlineMedium, color = Aubergine)
                        Text(gemstone.reason, modifier = Modifier.padding(top = 6.dp))
                        Text(gemstone.caution, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
                    }
                }
                Text("Low-risk traditional practices", style = MaterialTheme.typography.titleLarge)
            }
            items(lalKitab.remedies) { remedy ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 9.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(remedy.planet.displayName, style = MaterialTheme.typography.titleMedium)
                        Text(remedy.practice, modifier = Modifier.padding(top = 4.dp))
                        Text(remedy.note, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 5.dp))
                    }
                }
            }
            item {
                Text(
                    "No remedy should replace healthcare, legal, financial, or other qualified professional advice.",
                    color = Color(0xFF8A2E2E),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
        }
    }
}
