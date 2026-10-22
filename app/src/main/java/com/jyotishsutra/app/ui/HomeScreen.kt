package com.jyotishsutra.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PieChartOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.jyotishsutra.app.astrology.Horoscope
import com.jyotishsutra.app.astrology.NAKSHATRA_NAMES
import com.jyotishsutra.app.astrology.PlanetId
import com.jyotishsutra.app.ui.theme.Aubergine
import com.jyotishsutra.app.ui.theme.DeepPlum
import com.jyotishsutra.app.ui.theme.MutedGold
import com.jyotishsutra.app.ui.theme.Saffron

private data class Feature(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val screen: AppScreen,
)

@Composable
fun HomeScreen(
    horoscope: Horoscope,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier,
) {
    val moon = horoscope.planets.first { it.id == PlanetId.MOON }
    val features = listOf(
        Feature("Birth Chart", "Explore your kundali", Icons.Outlined.PieChartOutline, AppScreen.CREATE),
        Feature("Panchang", "Tithi, nakshatra, yoga", Icons.Outlined.CalendarMonth, AppScreen.PANCHANG),
        Feature("Dasha", "Planetary periods", Icons.Outlined.AutoAwesome, AppScreen.DASHA),
        Feature("Compatibility", "Check traditional harmony", Icons.Outlined.FavoriteBorder, AppScreen.MATCHING),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(DeepPlum, Aubergine, Color(0xFF9C443B), Saffron),
                    ),
                )
                .padding(24.dp),
        ) {
            ZodiacOrbit(Modifier.align(Alignment.TopEnd).size(190.dp))
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text("Jyotish Sutra", color = Color(0xFFFFD98A), style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(22.dp))
                Text(
                    "Namaste, ${horoscope.birthDetails.name}",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                )
                Text("Your cosmic blueprint", color = Color.White.copy(alpha = 0.86f))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .weight(1f),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clickable { onNavigate(AppScreen.REPORT) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0D9)),
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(shape = CircleShape, color = Aubergine, modifier = Modifier.size(52.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("☾", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                    Column(Modifier.padding(start = 14.dp).weight(1f)) {
                        Text(
                            "Moon in ${NAKSHATRA_NAMES[moon.nakshatraIndex]}",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            "Open your calculated chart and personal insights",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text("›", style = MaterialTheme.typography.headlineMedium)
                }
            }

            Text(
                "Explore",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(features) { feature ->
                    FeatureCard(feature = feature, onClick = { onNavigate(feature.screen) })
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(feature: Feature, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(132.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp)) {
            Surface(shape = CircleShape, color = Color(0xFFF3E1F6), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(feature.icon, contentDescription = null, tint = Aubergine, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(feature.title, style = MaterialTheme.typography.titleMedium)
            Text(
                feature.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun ZodiacOrbit(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val gold = MutedGold.copy(alpha = 0.65f)
        val center = Offset(size.width * 0.56f, size.height * 0.44f)
        repeat(3) { index ->
            drawCircle(
                color = gold,
                radius = size.minDimension * (0.22f + index * 0.10f),
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f),
            )
        }
        repeat(12) { index ->
            val angle = Math.toRadians(index * 30.0)
            val startRadius = size.minDimension * 0.22f
            val endRadius = size.minDimension * 0.42f
            drawLine(
                gold,
                Offset(center.x + kotlin.math.cos(angle).toFloat() * startRadius, center.y + kotlin.math.sin(angle).toFloat() * startRadius),
                Offset(center.x + kotlin.math.cos(angle).toFloat() * endRadius, center.y + kotlin.math.sin(angle).toFloat() * endRadius),
                strokeWidth = 1.2f,
            )
        }
        drawCircle(Color(0xFFFFC14D), radius = 22.dp.toPx(), center = center)
        drawCircle(Color(0xFFFFE6A5), radius = 10.dp.toPx(), center = center)
    }
}
