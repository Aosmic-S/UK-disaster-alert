package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SimulationScreen

@Serializable
object DashboardRoute

@Serializable
object HistoryRoute

@Serializable
object AdminRoute

@Serializable
object SettingsRoute

@Serializable
object SimulationRoute

@Composable
fun DisasterAlertApp(viewModel: MainViewModel) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF3B82F6)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("S3", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Column {
                        Text(
                            "DISASTER ALERT UK",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF34D399)))
                            Text(
                                "SYSTEM LIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399).copy(alpha = 0.8f),
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
                IconButton(
                    onClick = { 
                        navController.navigate(SettingsRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("STATUS", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) },
                    selected = currentDestination?.hierarchy?.any { it.route == DashboardRoute::class.qualifiedName } == true,
                    onClick = {
                        navController.navigate(DashboardRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.History, contentDescription = "History") },
                    label = { Text("HISTORY", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) },
                    selected = currentDestination?.hierarchy?.any { it.route == HistoryRoute::class.qualifiedName } == true,
                    onClick = {
                        navController.navigate(HistoryRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Explore, contentDescription = "Simulate") },
                    label = { Text("SIMULATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) },
                    selected = currentDestination?.hierarchy?.any { it.route == SimulationRoute::class.qualifiedName } == true,
                    onClick = {
                        navController.navigate(SimulationRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.AdminPanelSettings, contentDescription = "Admin") },
                    label = { Text("SECURE", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) },
                    selected = currentDestination?.hierarchy?.any { it.route == AdminRoute::class.qualifiedName } == true,
                    onClick = {
                        navController.navigate(AdminRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DashboardRoute,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            composable<DashboardRoute> { DashboardScreen(viewModel) }
            composable<HistoryRoute> { HistoryScreen(viewModel) }
            composable<SimulationRoute> { SimulationScreen() }
            composable<AdminRoute> { AdminScreen(viewModel) }
            composable<SettingsRoute> { SettingsScreen(viewModel) }
        }
    }
}
