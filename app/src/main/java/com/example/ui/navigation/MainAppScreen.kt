package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MultilineChart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.GridGoldenratio
import androidx.compose.material.icons.filled.MultilineChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AnalysisScreen
import com.example.ui.screens.IndicatorsScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.RiskCalculatorScreen
import com.example.ui.screens.TimeframeMatrixScreen
import com.example.ui.theme.QuantAmber
import com.example.ui.theme.QuantBorder
import com.example.ui.theme.QuantCyan
import com.example.ui.theme.QuantDarkBackground
import com.example.ui.theme.QuantDarkSurface
import com.example.ui.theme.QuantDarkSurfaceElevated
import com.example.ui.theme.QuantGreen
import com.example.ui.theme.QuantTextPrimary
import com.example.ui.theme.QuantTextSecondary
import com.example.ui.viewmodel.QuantViewModel
import kotlinx.coroutines.flow.collectLatest

enum class AppTab(val title: String, val icon: ImageVector) {
    ANALYZE("Analyze", Icons.Default.AutoAwesome),
    CALCULATOR("Risk Calc", Icons.Default.Calculate),
    MATRIX("Matrix", Icons.Default.GridGoldenratio),
    INDICATORS("Indicators", Icons.AutoMirrored.Filled.MultilineChart),
    JOURNAL("Journal", Icons.Default.Book)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: QuantViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(AppTab.ANALYZE) }
    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler(enabled = currentTab != AppTab.ANALYZE) {
        currentTab = AppTab.ANALYZE
    }

    LaunchedEffect(viewModel) {
        viewModel.eventMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = QuantDarkBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(QuantGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CRYPTO QUANT",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = QuantTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(QuantCyan.copy(alpha = 0.2f))
                                .border(1.dp, QuantCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "TERMINAL",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = QuantCyan
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = QuantDarkSurface,
                    titleContentColor = QuantTextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = QuantDarkSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(1.dp, QuantBorder.copy(alpha = 0.5f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .testTag("main_bottom_nav")
            ) {
                AppTab.entries.forEach { tab ->
                    val selected = currentTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = QuantCyan,
                            indicatorColor = QuantCyan,
                            unselectedIconColor = QuantTextSecondary,
                            unselectedTextColor = QuantTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    AppTab.ANALYZE -> AnalysisScreen(viewModel = viewModel)
                    AppTab.CALCULATOR -> RiskCalculatorScreen(viewModel = viewModel)
                    AppTab.MATRIX -> TimeframeMatrixScreen(viewModel = viewModel)
                    AppTab.INDICATORS -> IndicatorsScreen()
                    AppTab.JOURNAL -> JournalScreen(viewModel = viewModel)
                }
            }
        }
    }
}
