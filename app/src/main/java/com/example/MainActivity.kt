package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.FocusedTerminalScreen
import com.example.ui.screens.GridScreen
import com.example.ui.screens.JobsScreen
import com.example.ui.screens.MemoryWorkspaceScreen
import com.example.ui.screens.OrchestratorScreen
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanGlow
import com.example.ui.theme.MissionControlTheme
import com.example.ui.theme.SleekPillBg
import com.example.ui.theme.SleekPillText
import com.example.ui.theme.SleekTerminalBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MissionControlViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MissionControlTheme {
                MissionControlApp()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Grid : Screen("grid", "6-TTY Grid", Icons.Default.GridOn)
    object Terminal : Screen("terminal", "Focused TTY", Icons.Default.Terminal)
    object Orchestrate : Screen("orchestrate", "Team Room", Icons.Default.Groups)
    object Jobs : Screen("jobs", "Jobs Inbox", Icons.Default.Inbox)
    object Memory : Screen("memory", "Memory & Assets", Icons.Default.Memory)
}

@Composable
fun MissionControlApp() {
    val navController = rememberNavController()
    val viewModel: MissionControlViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        Screen.Grid,
        Screen.Terminal,
        Screen.Orchestrate,
        Screen.Jobs,
        Screen.Memory
    )

    Scaffold(
        containerColor = CyberBg,
        bottomBar = {
            NavigationBar(
                containerColor = SleekTerminalBg,
                contentColor = TextPrimary
            ) {
                items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SleekPillText,
                            selectedTextColor = TextPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = SleekPillBg
                        )
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Grid.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Grid.route) {
                GridScreen(
                    viewModel = viewModel,
                    onNavigateToTTY = { slot ->
                        navController.navigate(Screen.Terminal.route)
                    }
                )
            }
            composable(Screen.Terminal.route) {
                FocusedTerminalScreen(viewModel = viewModel)
            }
            composable(Screen.Orchestrate.route) {
                OrchestratorScreen(viewModel = viewModel)
            }
            composable(Screen.Jobs.route) {
                JobsScreen(viewModel = viewModel)
            }
            composable(Screen.Memory.route) {
                MemoryWorkspaceScreen(viewModel = viewModel)
            }
        }
    }
}
