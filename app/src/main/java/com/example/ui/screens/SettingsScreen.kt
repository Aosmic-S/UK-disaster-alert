package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("GENERAL SETTINGS", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        SettingsSection("DISPLAY & THEME") {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Theme Style", fontSize = 14.sp)
                val isImmersive = settings.themeMode == "Immersive"
                Switch(
                    checked = isImmersive,
                    onCheckedChange = {
                        val newTheme = if (it) "Immersive" else "System"
                        viewModel.updateSettings(settings.copy(themeMode = newTheme))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        uncheckedThumbColor = Color(0xFF94A3B8),
                        uncheckedTrackColor = Color(0xFF334155)
                    )
                )
            }
            Text("Switch to 'System' for standard UI (Preview Demo)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        SettingsSection("NOTIFICATIONS & ALERTS") {
            Text("All alert sounds and vibration notifications are managed by the Android System. Make sure your device volume is up.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        SettingsSection("ABOUT") {
            Text("Disaster Alert Pro v1.2", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Real-time telemetry and hazard detection.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Color(0xFF334155).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            content()
        }
    }
}
