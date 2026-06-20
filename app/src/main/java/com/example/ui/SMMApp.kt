package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.SocialPost

@Composable
fun SMMApp(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("smm_app_shell"),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .testTag("bottom_nav_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = viewModel.selectedTab == 0,
                    onClick = { viewModel.selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.selectedTab == 0) Icons.Default.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "Estudio"
                        )
                    },
                    label = { Text("Estudio") },
                    modifier = Modifier.testTag("tab_dashboard")
                )

                NavigationBarItem(
                    selected = viewModel.selectedTab == 1,
                    onClick = { viewModel.selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.selectedTab == 1) Icons.Default.CalendarMonth else Icons.Outlined.CalendarMonth,
                            contentDescription = "Planificador"
                        )
                    },
                    label = { Text("Calendario") },
                    modifier = Modifier.testTag("tab_calendar")
                )

                NavigationBarItem(
                    selected = viewModel.selectedTab == 2,
                    onClick = { viewModel.selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.selectedTab == 2) Icons.Default.EditNote else Icons.Outlined.EditNote,
                            contentDescription = "Creador"
                        )
                    },
                    label = { Text("Redactar") },
                    modifier = Modifier.testTag("tab_editor")
                )

                NavigationBarItem(
                    selected = viewModel.selectedTab == 3,
                    onClick = { viewModel.selectedTab = 3 },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.selectedTab == 3) Icons.Default.Settings else Icons.Outlined.Settings,
                            contentDescription = "Configuración"
                        )
                    },
                    label = { Text("Ajustes") },
                    modifier = Modifier.testTag("tab_settings")
                )
            }
        }
    ) { innerPadding ->
        // Animated transition between tabs
        AnimatedContent(
            targetState = viewModel.selectedTab,
            transitionSpec = {
                fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars),
            label = "tab_transitions"
        ) { tabIndex ->
            when (tabIndex) {
                0 -> DashboardScreen(viewModel = viewModel)
                1 -> CalendarScreen(
                    viewModel = viewModel,
                    onNavigateToEditor = { targetTimestamp ->
                        viewModel.editingPost = SocialPost(
                            title = "",
                            content = "",
                            scheduledTime = targetTimestamp,
                            platforms = "instagram",
                            status = "DRAFT"
                        )
                        viewModel.selectedTab = 2 // Redirect to creator tab
                    }
                )
                2 -> PostEditorScreen(viewModel = viewModel)
                3 -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
