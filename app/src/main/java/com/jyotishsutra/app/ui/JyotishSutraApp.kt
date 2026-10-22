package com.jyotishsutra.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jyotishsutra.app.astrology.Ayanamsa
import com.jyotishsutra.app.astrology.BirthDetails
import com.jyotishsutra.app.astrology.Horoscope
import com.jyotishsutra.app.astrology.HoroscopeCalculator
import java.time.LocalDate
import java.time.LocalTime

enum class AppScreen {
    HOME, CREATE, REPORT, PANCHANG, DASHA, MATCHING, KP, LAL_KITAB, REMEDIES, TOOLS, SETTINGS,
}

private data class BottomDestination(
    val screen: AppScreen,
    val label: String,
    val icon: @Composable () -> Unit,
)

@Composable
fun JyotishSutraApp() {
    val calculator = remember { HoroscopeCalculator() }
    val sample = remember {
        calculator.calculate(
            BirthDetails(
                name = "Arjun",
                date = LocalDate.of(1990, 5, 15),
                time = LocalTime.of(10, 30),
                zoneOffsetMinutes = 330,
                latitude = 28.6139,
                longitude = 77.2090,
                placeName = "New Delhi, India",
            ),
        )
    }
    var screen by remember { mutableStateOf(AppScreen.HOME) }
    var horoscope by remember { mutableStateOf(sample) }
    var ayanamsa by remember { mutableStateOf(Ayanamsa.LAHIRI) }

    BackHandler(enabled = screen != AppScreen.HOME) { screen = AppScreen.HOME }

    val bottomDestinations = listOf(
        BottomDestination(AppScreen.HOME, "Home") {
            Icon(Icons.Outlined.Home, contentDescription = null)
        },
        BottomDestination(AppScreen.REPORT, "Insights") {
            Icon(Icons.Outlined.Insights, contentDescription = null)
        },
        BottomDestination(AppScreen.TOOLS, "Tools") {
            Icon(Icons.Outlined.GridView, contentDescription = null)
        },
        BottomDestination(AppScreen.SETTINGS, "Profile") {
            Icon(Icons.Outlined.PersonOutline, contentDescription = null)
        },
    )

    Scaffold(
        bottomBar = {
            if (screen != AppScreen.CREATE) {
                NavigationBar {
                    bottomDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = screen == destination.screen,
                            onClick = { screen = destination.screen },
                            icon = destination.icon,
                            label = { Text(destination.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        when (screen) {
            AppScreen.HOME -> HomeScreen(
                horoscope = horoscope,
                modifier = Modifier.padding(padding),
                onNavigate = { screen = it },
            )

            AppScreen.CREATE -> BirthFormScreen(
                initialAyanamsa = ayanamsa,
                modifier = Modifier.padding(padding),
                onBack = { screen = AppScreen.HOME },
                onCalculate = { details, selectedAyanamsa ->
                    ayanamsa = selectedAyanamsa
                    horoscope = calculator.calculate(details, selectedAyanamsa)
                    screen = AppScreen.REPORT
                },
            )

            AppScreen.REPORT -> HoroscopeScreen(
                horoscope = horoscope,
                modifier = Modifier.padding(padding),
                onCreateNew = { screen = AppScreen.CREATE },
            )

            AppScreen.PANCHANG -> PanchangScreen(
                horoscope = horoscope,
                modifier = Modifier.padding(padding),
                onBack = { screen = AppScreen.HOME },
            )

            AppScreen.DASHA -> DashaScreen(
                horoscope = horoscope,
                modifier = Modifier.padding(padding),
                onBack = { screen = AppScreen.HOME },
            )

            AppScreen.MATCHING -> CompatibilityScreen(
                primary = horoscope.birthDetails,
                modifier = Modifier.padding(padding),
                onBack = { screen = AppScreen.HOME },
            )

            AppScreen.KP -> KpScreen(
                horoscope = horoscope,
                modifier = Modifier.padding(padding),
                onBack = { screen = AppScreen.TOOLS },
            )

            AppScreen.LAL_KITAB -> LalKitabScreen(
                horoscope = horoscope,
                modifier = Modifier.padding(padding),
                onBack = { screen = AppScreen.TOOLS },
            )

            AppScreen.REMEDIES -> RemediesScreen(
                horoscope = horoscope,
                modifier = Modifier.padding(padding),
                onBack = { screen = AppScreen.TOOLS },
            )

            AppScreen.TOOLS -> ToolsScreen(
                modifier = Modifier.padding(padding),
                onNavigate = { screen = it },
            )

            AppScreen.SETTINGS -> SettingsScreen(
                selectedAyanamsa = ayanamsa,
                onAyanamsaChange = { ayanamsa = it },
                modifier = Modifier.padding(padding),
            )
        }
    }
}
