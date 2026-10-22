package com.jyotishsutra.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jyotishsutra.app.astrology.Ayanamsa
import com.jyotishsutra.app.astrology.BirthDetails
import com.jyotishsutra.app.ui.theme.Aubergine
import com.jyotishsutra.app.ui.theme.Saffron
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun BirthFormScreen(
    initialAyanamsa: Ayanamsa,
    onBack: () -> Unit,
    onCalculate: (BirthDetails, Ayanamsa) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("1990-05-15") }
    var time by remember { mutableStateOf("10:30") }
    var place by remember { mutableStateOf("New Delhi, India") }
    var latitude by remember { mutableStateOf("28.6139") }
    var longitude by remember { mutableStateOf("77.2090") }
    var utcOffset by remember { mutableStateOf("+05:30") }
    var ayanamsa by remember { mutableStateOf(initialAyanamsa) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
            }
        }
        Text("Create Kundali", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Enter accurate birth details to generate a sidereal Vedic chart.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 22.dp),
        )

        FormField("Name", name, { name = it }, "Arjun")
        FormField("Birth date", date, { date = it }, "YYYY-MM-DD")
        FormField("Birth time", time, { time = it }, "HH:MM")
        FormField("Birth place", place, { place = it }, "City, Country")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormField(
                label = "Latitude",
                value = latitude,
                onChange = { latitude = it },
                placeholder = "28.6139",
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Decimal,
            )
            FormField(
                label = "Longitude",
                value = longitude,
                onChange = { longitude = it },
                placeholder = "77.2090",
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Decimal,
            )
        }
        FormField("UTC offset", utcOffset, { utcOffset = it }, "+05:30")

        Text(
            "Ayanamsha",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Ayanamsa.entries.forEach { option ->
                FilterChip(
                    selected = ayanamsa == option,
                    onClick = { ayanamsa = option },
                    label = { Text(option.displayName) },
                )
            }
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 10.dp),
            )
        }

        Button(
            onClick = {
                runCatching {
                    BirthDetails(
                        name = name.trim().ifBlank { "Guest" },
                        date = LocalDate.parse(date.trim()),
                        time = LocalTime.parse(time.trim()),
                        zoneOffsetMinutes = parseOffsetMinutes(utcOffset),
                        latitude = latitude.toDouble(),
                        longitude = longitude.toDouble(),
                        placeName = place.trim(),
                    )
                }.onSuccess {
                    error = null
                    onCalculate(it, ayanamsa)
                }.onFailure {
                    error = "Please check the date, time, UTC offset, latitude, and longitude."
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 22.dp).height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(Aubergine, Saffron))),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = null)
                Text("Calculate Chart", modifier = Modifier.padding(start = 10.dp))
            }
        }
        Text(
            "Astrology results are intended for cultural and reflective use.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 18.dp),
        )
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth().padding(bottom = 12.dp),
    )
}

private fun parseOffsetMinutes(value: String): Int {
    val cleaned = value.trim()
    val sign = if (cleaned.startsWith("-")) -1 else 1
    val unsigned = cleaned.removePrefix("+").removePrefix("-")
    val parts = unsigned.split(":")
    require(parts.size == 2)
    val hours = parts[0].toInt()
    val minutes = parts[1].toInt()
    require(hours in 0..18 && minutes in 0..59)
    return sign * (hours * 60 + minutes)
}
