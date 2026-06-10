package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SensorHistoryEntity
import com.example.ui.MainViewModel

@Composable
fun HistoryScreen(viewModel: MainViewModel) {
    val history by viewModel.history.collectAsState()
    var viewMode by remember { mutableStateOf("Tabular") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("DATA HISTORY", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        TabRow(
            selectedTabIndex = if (viewMode == "Tabular") 0 else 1,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[if (viewMode == "Tabular") 0 else 1]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Tab(selected = viewMode == "Tabular", onClick = { viewMode = "Tabular" }, text = { Text("TABULAR", fontSize = 12.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = viewMode == "Visual", onClick = { viewMode = "Visual" }, text = { Text("VISUAL", fontSize = 12.sp, fontWeight = FontWeight.Bold) })
        }

        if (viewMode == "Tabular") {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(history) { item ->
                    HistoryItemCard(item)
                }
            }
        } else {
            if (history.isNotEmpty()) {
                SimpleLineChart(history.reversed())
            } else {
                Text("No data available for chart", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun SimpleLineChart(history: List<SensorHistoryEntity>) {
    val points = history.mapNotNull { it.field1 }
    if (points.isEmpty()) return

    val maxVal = points.maxOrNull() ?: 1f
    val minVal = points.minOrNull() ?: 0f
    
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Color(0xFF334155).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(
                "SOIL WATER CAPACITY TREND",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val xStep = width / (points.size.coerceAtLeast(2) - 1).coerceAtLeast(1)
                
                val path = Path()
                points.forEachIndexed { index, value ->
                    val normalizedValue = (value - minVal) / (maxVal - minVal).coerceAtLeast(1f)
                    val x = index * xStep
                    val y = height - (normalizedValue * height)
                    
                    if (index == 0) path.moveTo(x, y)
                    else path.lineTo(x, y)
                }
                
                drawPath(
                    path = path,
                    color = primaryColor,
                    style = Stroke(width = 4f)
                )
            }
        }
    }
}

@Composable
fun HistoryItemCard(entry: SensorHistoryEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Color(0xFF334155).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Time: ${entry.createdAt}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Landslide: ${entry.field1 ?: "N/A"}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("Bridge: ${entry.field2 ?: "N/A"}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(
                "Earthquake: X:${entry.field3 ?: "0"} Y:${entry.field4 ?: "0"} Z:${entry.field5 ?: "0"}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f)
            )
        }
    }
}
