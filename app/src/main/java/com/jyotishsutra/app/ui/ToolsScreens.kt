package com.jyotishsutra.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Brightness5
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jyotishsutra.app.astrology.Ayanamsa
import com.jyotishsutra.app.astrology.BirthDetails
import com.jyotishsutra.app.astrology.CompatibilityCalculator
import com.jyotishsutra.app.astrology.CompatibilityResult
import com.jyotishsutra.app.ui.theme.Aubergine
import java.time.LocalDate
import java.time.LocalTime

private data class ToolItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val available: Boolean,
    val destination: AppScreen? = null,
)

@Composable
fun ToolsScreen(
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tools = listOf(
        ToolItem("Vedic Kundali", "Rashi chart, planets and houses", Icons.Outlined.Brightness5, true, AppScreen.CREATE),
        ToolItem("Vimshottari Dasha", "Major planetary periods", Icons.Outlined.Timeline, true, AppScreen.DASHA),
        ToolItem("Marriage Matching", "Ashtakoota score out of 36", Icons.Outlined.FavoriteBorder, true, AppScreen.MATCHING),
        ToolItem("Daily Panchang", "Tithi, nakshatra, yoga and karana", Icons.Outlined.AutoAwesome, true, AppScreen.PANCHANG),
        ToolItem("Lal Kitab", "Fixed-house chart and safe practices", Icons.Outlined.MenuBook, true, AppScreen.LAL_KITAB),
        ToolItem("KP Astrology", "Star lord, sub lord and sub-sub lord", Icons.Outlined.Hub, true, AppScreen.KP),
        ToolItem("Remedies", "Gemstone and traditional guidance", Icons.Outlined.Spa, true, AppScreen.REMEDIES),
        ToolItem("Gemini Guide", "Plain-language chart explanations", Icons.Outlined.Psychology, false),
    )
    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
        Text(
            "Astrology Tools",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(tools) { tool ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(168.dp)
                        .clickable(enabled = tool.available) { tool.destination?.let(onNavigate) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                            Icon(
                                tool.icon,
                                contentDescription = null,
                                tint = Aubergine,
                                modifier = Modifier.padding(10.dp),
                            )
                        }
                        Text(tool.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp))
                        Text(
                            tool.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                        )
                        if (!tool.available) {
                            Text(
                                "Next milestone",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(top = 5.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompatibilityScreen(
    primary: BirthDetails,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val calculator = remember { CompatibilityCalculator() }
    var secondName by remember { mutableStateOf("Priya") }
    var secondDate by remember { mutableStateOf("1992-08-21") }
    var secondTime by remember { mutableStateOf("14:15") }
    var result by remember { mutableStateOf<CompatibilityResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ScreenHeader("Compatibility", onBack)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        ) {
            Text(
                "Ashtakoota matching",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                "Comparing with ${primary.name}'s saved chart. Enter the second person's details.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 14.dp),
            )
            OutlinedTextField(
                value = secondName,
                onValueChange = { secondName = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            )
            OutlinedTextField(
                value = secondDate,
                onValueChange = { secondDate = it },
                label = { Text("Birth date (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            )
            OutlinedTextField(
                value = secondTime,
                onValueChange = { secondTime = it },
                label = { Text("Birth time (HH:MM)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            )
            Button(
                onClick = {
                    runCatching {
                        val second = primary.copy(
                            name = secondName.ifBlank { "Person 2" },
                            date = LocalDate.parse(secondDate),
                            time = LocalTime.parse(secondTime),
                        )
                        calculator.calculate(primary, second)
                    }.onSuccess {
                        result = it
                        error = null
                    }.onFailure {
                        error = "Please enter a valid ISO date and 24-hour time."
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
            ) {
                Text("Calculate Compatibility")
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
            result?.let { CompatibilityResultCard(it) }
        }
    }
}

@Composable
private fun CompatibilityResultCard(result: CompatibilityResult) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text("${String.format("%.1f", result.total)} / 36", style = MaterialTheme.typography.headlineLarge, color = Aubergine)
            Text(result.verdict, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 14.dp))
            result.scores.forEach { score ->
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(score.name, fontWeight = FontWeight.SemiBold)
                        Text(score.detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("${score.earned} / ${score.maximum}")
                }
            }
        }
    }
}

private data class AppLanguage(val englishName: String, val nativeName: String, val tag: String)

private val INDIAN_LANGUAGES = listOf(
    AppLanguage("English", "English", "en"),
    AppLanguage("Assamese", "অসমীয়া", "as"),
    AppLanguage("Bengali", "বাংলা", "bn"),
    AppLanguage("Bodo", "बड़ो", "brx"),
    AppLanguage("Dogri", "डोगरी", "doi"),
    AppLanguage("Gujarati", "ગુજરાતી", "gu"),
    AppLanguage("Hindi", "हिन्दी", "hi"),
    AppLanguage("Kannada", "ಕನ್ನಡ", "kn"),
    AppLanguage("Kashmiri", "کٲشُر", "ks"),
    AppLanguage("Konkani", "कोंकणी", "kok"),
    AppLanguage("Maithili", "मैथिली", "mai"),
    AppLanguage("Malayalam", "മലയാളം", "ml"),
    AppLanguage("Manipuri", "মৈতৈলোন্", "mni"),
    AppLanguage("Marathi", "मराठी", "mr"),
    AppLanguage("Nepali", "नेपाली", "ne"),
    AppLanguage("Odia", "ଓଡ଼ିଆ", "or"),
    AppLanguage("Punjabi", "ਪੰਜਾਬੀ", "pa"),
    AppLanguage("Sanskrit", "संस्कृतम्", "sa"),
    AppLanguage("Santali", "ᱥᱟᱱᱛᱟᱲᱤ", "sat"),
    AppLanguage("Sindhi", "سنڌي", "sd"),
    AppLanguage("Tamil", "தமிழ்", "ta"),
    AppLanguage("Telugu", "తెలుగు", "te"),
    AppLanguage("Urdu", "اردو", "ur"),
)

@Composable
fun SettingsScreen(
    selectedAyanamsa: Ayanamsa,
    onAyanamsaChange: (Ayanamsa) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedLanguage by remember { mutableStateOf("en") }
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Text(
            "Profile & Settings",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
        )
        LazyColumn(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp)) {
            item {
                Text("Calculation standard", style = MaterialTheme.typography.titleLarge)
                Ayanamsa.entries.forEach { option ->
                    FilterChip(
                        selected = selectedAyanamsa == option,
                        onClick = { onAyanamsaChange(option) },
                        label = { Text(option.displayName) },
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
                Text("Language", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 18.dp, bottom = 10.dp))
            }
            items(INDIAN_LANGUAGES.size) { index ->
                val language = INDIAN_LANGUAGES[index]
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { selectedLanguage = language.tag },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedLanguage == language.tag) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                    ),
                ) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(language.nativeName, style = MaterialTheme.typography.titleMedium)
                            if (language.nativeName != language.englishName) {
                                Text(language.englishName, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        if (selectedLanguage == language.tag) Text("✓", color = Aubergine, fontWeight = FontWeight.Bold)
                    }
                }
            }
            item {
                Text(
                    "Translation resources are being rolled out across all 22 scheduled Indian languages plus English.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 14.dp),
                )
            }
        }
    }
}
