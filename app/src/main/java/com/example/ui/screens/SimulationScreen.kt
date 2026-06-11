package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale

data class SimulatedLocation(val name: String, val type: String, var status: String, var value: String, val isCritical: Boolean)

@Composable
fun SimulationScreen() {
    var baseLocations by remember { mutableStateOf(generateSimulatedData()) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while(true) {
            delay(5000)
            baseLocations = baseLocations.map { loc -> 
                // Only update the default ones or keep them somewhat random
                generateRandomStatusFor(loc.name, loc.type)
            }
        }
    }

    val filteredLocations = remember(searchQuery, baseLocations) {
        if (searchQuery.isBlank()) {
            baseLocations
        } else {
            val matches = baseLocations.filter { it.name.lowercase(Locale.ROOT).contains(searchQuery.lowercase(Locale.ROOT)) }
            if (matches.isEmpty()) {
                listOf(generateRandomStatusFor(searchQuery, "Custom Location"))
            } else {
                matches
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("SIMULATED LOCATIONS", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search specific location...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color(0xFF334155),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Text("Note: This section monitors simulated locations only. The rest of the app operates on real-time data.", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredLocations, key = { it.name }) { loc ->
                LocationCard(loc)
            }
        }
    }
}

fun generateRandomStatusFor(name: String, type: String): SimulatedLocation {
    val r = java.util.Random()
    val v = r.nextInt(100)
    val thresh = if (type == "Bridge") 80 else 90
    val isCritical = v > thresh
    val status = if (isCritical) "CRITICAL" else "STABLE"
    val valLabel = when(type) {
        "Bridge" -> "Stress Level"
        "Mountain" -> "Seismic Activity"
        "Water Level" -> "Capacity"
        "Landslide" -> "Soil Moisture"
        else -> "Risk Factor"
    }
    val valStr = if (type == "Mountain") "0.$v g" else "$v%"
    return SimulatedLocation(name, type, status, "$valLabel: $valStr", isCritical)
}

fun generateSimulatedData(): List<SimulatedLocation> {
    return listOf(
        generateRandomStatusFor("London Millennium Bridge", "Bridge"),
        generateRandomStatusFor("Ben Nevis Summit", "Mountain"),
        generateRandomStatusFor("Thames Barrier", "Water Level"),
        generateRandomStatusFor("Severn Bridge", "Bridge"),
        generateRandomStatusFor("Edinburgh Castle Rock", "Landslide")
    )
}

@Composable
fun LocationCard(loc: SimulatedLocation) {
    val borderColor = if (loc.isCritical) MaterialTheme.colorScheme.error.copy(alpha = 0.3f) else Color(0xFF334155).copy(alpha = 0.5f)
    val bgColor = if (loc.isCritical) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
    val statusColor = if (loc.isCritical) MaterialTheme.colorScheme.error else Color(0xFF34D399) 

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(loc.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(loc.type.uppercase(), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(loc.value, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.8f))
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(loc.status, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
