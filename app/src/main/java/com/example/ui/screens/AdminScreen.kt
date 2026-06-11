package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@Composable
fun AdminScreen(viewModel: MainViewModel) {
    var isAuthenticated by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    if (!isAuthenticated) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.Lock, contentDescription = "Lock", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("SECURE ACCESS", fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it },
                label = { Text("Admin Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = errorMessage.isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFF334155)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (passwordInput == "Absolute01") {
                        isAuthenticated = true
                        errorMessage = ""
                    } else {
                        errorMessage = "Access Denied"
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f).height(48.dp)
            ) {
                Text("AUTHENTICATE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    } else {
        AdminDashboard(viewModel)
    }
}

@Composable
fun AdminDashboard(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()
    
    var goodThreshold by remember(settings.goodThreshold) { mutableStateOf(settings.goodThreshold.toString()) }
    var criticalThreshold by remember(settings.criticalThreshold) { mutableStateOf(settings.criticalThreshold.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("SYSTEM CONFIGURATION", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        AdminSection("THRESHOLD CALIBRATION") {
            OutlinedTextField(
                value = goodThreshold,
                onValueChange = { goodThreshold = it },
                label = { Text("Good Threshold (< value)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF334155)),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = criticalThreshold,
                onValueChange = { criticalThreshold = it },
                label = { Text("Critical Threshold (> value)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF334155)),
                shape = RoundedCornerShape(12.dp)
            )
            Button(
                onClick = {
                    val g = goodThreshold.toFloatOrNull() ?: settings.goodThreshold
                    val c = criticalThreshold.toFloatOrNull() ?: settings.criticalThreshold
                    viewModel.updateSettings(settings.copy(goodThreshold = g, criticalThreshold = c))
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("SAVE CALIBRATION")
            }
        }

        AdminSection("SENSOR MODULES") {
            var lName by remember(settings.landslideName) { mutableStateOf(settings.landslideName) }
            var bName by remember(settings.bridgeName) { mutableStateOf(settings.bridgeName) }
            var eName by remember(settings.earthquakeName) { mutableStateOf(settings.earthquakeName) }

            AdminSwitchItemWithRename(
                name = lName,
                onNameChange = { lName = it },
                checked = settings.isLandslideActive,
                onCheckedChange = { viewModel.updateSettings(settings.copy(isLandslideActive = it, landslideName = lName)) },
                onSave = { viewModel.updateSettings(settings.copy(landslideName = lName)) }
            )
            AdminSwitchItemWithRename(
                name = bName,
                onNameChange = { bName = it },
                checked = settings.isBridgeActive,
                onCheckedChange = { viewModel.updateSettings(settings.copy(isBridgeActive = it, bridgeName = bName)) },
                onSave = { viewModel.updateSettings(settings.copy(bridgeName = bName)) }
            )
            AdminSwitchItemWithRename(
                name = eName,
                onNameChange = { eName = it },
                checked = settings.isEarthquakeActive,
                onCheckedChange = { viewModel.updateSettings(settings.copy(isEarthquakeActive = it, earthquakeName = eName)) },
                onSave = { viewModel.updateSettings(settings.copy(earthquakeName = eName)) }
            )
        }

        AdminSection("DATA EXFILTRATION") {
            AdminSwitchItem("Public Telemetry Access", settings.publicDataEnabled) { 
                viewModel.updateSettings(settings.copy(publicDataEnabled = it)) 
            }
        }
        
        AdminSection("IDENTITY MANAGEMENT") {
            var newUsername by remember { mutableStateOf("") }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = { newUsername = it },
                    label = { Text("New Identifier") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (newUsername.isNotBlank()) {
                            viewModel.addAdminUser(newUsername)
                            newUsername = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("AUTHORIZE")
                }
            }
            
            val adminUsers by viewModel.adminUsers.collectAsState()
            
            Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                adminUsers.forEach { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(user, fontWeight = FontWeight.SemiBold)
                        if (user != "Admin") {
                            TextButton(onClick = { viewModel.removeAdminUser(user) }) {
                                Text("REVOKE", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else {
                            Text("ROOT", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Color(0xFF334155).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}

@Composable
fun AdminSwitchItemWithRename(name: String, onNameChange: (String) -> Unit, checked: Boolean, onCheckedChange: (Boolean) -> Unit, onSave: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = Color(0xFF1E293B),
                    focusedContainerColor = Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(8.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    uncheckedThumbColor = Color(0xFF94A3B8),
                    uncheckedTrackColor = Color(0xFF334155)
                )
            )
        }
        TextButton(onClick = onSave, modifier = Modifier.align(Alignment.End)) {
            Text("SAVE RENAME", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun AdminSwitchItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 14.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFF334155)
            )
        )
    }
}
