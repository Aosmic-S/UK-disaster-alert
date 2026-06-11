package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val history by viewModel.history.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    val latest = history.firstOrNull()
    val aiInsight by viewModel.aiInsight.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (aiInsight != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, Color(0xFF334155).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "AI INTELLIGENCE SUMMARY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            aiInsight ?: "",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = "AI Insight", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(if (isConnected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.errorContainer)
                .border(
                    1.dp,
                    if (isConnected) Color(0xFF334155).copy(alpha = 0.5f) else MaterialTheme.colorScheme.error,
                    RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = if (isConnected) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                    contentDescription = null,
                    tint = if (isConnected) Color(0xFF34D399) else MaterialTheme.colorScheme.error
                )
                Text(
                    text = if (isConnected) "SYSTEM CONNECTED" else "SYSTEM DISCONNECTED",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = if (isConnected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                )
            }
        }

        if (latest != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                if (settings.isLandslideActive) {
                    val soilVal = latest.field1 ?: 0f
                    val status = when {
                        soilVal < settings.goodThreshold -> "STABLE"
                        soilVal > settings.criticalThreshold -> "CRITICAL"
                        else -> "WARNING"
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SensorMetricCard(
                            label = settings.landslideName,
                            value = soilVal.toString(),
                            subtitle = "Saturation",
                            status = status,
                            isCritical = soilVal > settings.criticalThreshold
                        )
                    }
                }

                if (settings.isBridgeActive) {
                    val erosionVal = latest.field2 ?: 0f
                    Box(modifier = Modifier.weight(1f)) {
                        SensorMetricCard(
                            label = settings.bridgeName,
                            value = erosionVal.toString(),
                            subtitle = "Erosion",
                            status = "ACTIVE",
                            isCritical = false // Assuming no specific threshold specified, generic ACTIVE
                        )
                    }
                }
            }

            if (settings.isEarthquakeActive) {
                val ex = latest.field3 ?: 0f
                val ey = latest.field4 ?: 0f
                val ez = latest.field5 ?: 0f
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, Color(0xFF334155).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                settings.earthquakeName.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CoordinateBadge("X: $ex", Color(0xFF3B82F6))
                                CoordinateBadge("Y: $ey", Color(0xFFA855F7))
                                CoordinateBadge("Z: $ez", Color(0xFFEC4899))
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                // Simplified representation of activity
                                Box(modifier = Modifier.width(4.dp).height((extrapolate(ex) * 40).dp.coerceAtLeast(10.dp)).clip(RoundedCornerShape(50)).background(Color(0xFF3B82F6)))
                                Box(modifier = Modifier.width(4.dp).height((extrapolate(ey) * 40).dp.coerceAtLeast(10.dp)).clip(RoundedCornerShape(50)).background(Color(0xFFA855F7)))
                                Box(modifier = Modifier.width(4.dp).height((extrapolate(ez) * 40).dp.coerceAtLeast(10.dp)).clip(RoundedCornerShape(50)).background(Color(0xFFEC4899)))
                            }
                        }
                    }
                }
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = MaterialTheme.colorScheme.primary)
        }
    }
}

// helper
fun extrapolate(v: Float) = (Math.abs(v) % 2).toFloat() + 0.5f

@Composable
fun CoordinateBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text, fontSize = 10.sp, color = color, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun SensorMetricCard(label: String, value: String, subtitle: String, status: String, isCritical: Boolean) {
    val borderColor = if (isCritical) MaterialTheme.colorScheme.error.copy(alpha = 0.3f) else Color(0xFF334155).copy(alpha = 0.5f)
    val bgColor = if (isCritical) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
    val valueColor = if (isCritical) MaterialTheme.colorScheme.error else Color(0xFF34D399) // Emerald
    val statusColor = if (isCritical) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(
                label.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCritical) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "$subtitle ($status)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = statusColor,
                letterSpacing = 0.5.sp,
                modifier = Modifier.alpha(if (isCritical) 1f else 0.8f)
            )
        }
    }
}
