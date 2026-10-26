package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.PositionRiskResult
import com.example.data.model.SignalType
import com.example.data.model.TradeSignal
import com.example.ui.components.MetricBox
import com.example.ui.components.PositionSizingCard
import com.example.ui.components.PriceLevelRow
import com.example.ui.components.QuantCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.SignalBadge
import com.example.ui.theme.QuantAmber
import com.example.ui.theme.QuantBorder
import com.example.ui.theme.QuantCyan
import com.example.ui.theme.QuantDarkBackground
import com.example.ui.theme.QuantDarkSurface
import com.example.ui.theme.QuantDarkSurfaceElevated
import com.example.ui.theme.QuantGreen
import com.example.ui.theme.QuantLime
import com.example.ui.theme.QuantRed
import com.example.ui.theme.QuantRedDark
import com.example.ui.theme.QuantRedGlow
import com.example.ui.theme.QuantTextMuted
import com.example.ui.theme.QuantTextPrimary
import com.example.ui.theme.QuantTextSecondary
import com.example.ui.viewmodel.AnalysisUiState
import com.example.ui.viewmodel.QuantViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    viewModel: QuantViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val analysisState by viewModel.analysisState.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val assetPair by viewModel.assetPair.collectAsState()
    val timeframe by viewModel.timeframe.collectAsState()
    val accountBalance by viewModel.accountBalance.collectAsState()
    val riskPercentage by viewModel.riskPercentage.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setImageUri(uri)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuantDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Terminal Header
        QuantCard(
            borderColor = QuantCyan.copy(alpha = 0.4f),
            modifier = Modifier.testTag("hero_terminal_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "VISUAL CHART ANALYST",
                        color = QuantCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Quant Signal Studio",
                        color = QuantTextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "20/50/200 EMAs • RSI Divergences • Volume Profile • Exact Risk Math",
                        color = QuantTextSecondary,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(QuantDarkSurfaceElevated)
                        .border(1.dp, QuantCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Quant",
                        tint = QuantCyan,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        // Chart Source Selector (Upload or Sample Presets)
        QuantCard {
            Text(
                text = "1. SELECT OR UPLOAD CANDLESTICK CHART",
                color = QuantCyan,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Preset Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetChip(
                    title = "BTC 4H (Binance)",
                    subtitle = "20/50/200 EMA Bounce",
                    isSelected = selectedPreset == "btc_4h" && selectedImageUri == null,
                    onClick = { viewModel.selectPreset("btc_4h") }
                )
                PresetChip(
                    title = "ETH 1H (TradingView)",
                    subtitle = "Bearish Divergence",
                    isSelected = selectedPreset == "eth_1h" && selectedImageUri == null,
                    onClick = { viewModel.selectPreset("eth_1h") }
                )
                PresetChip(
                    title = "SOL 12H (Macro)",
                    subtitle = "HVN Support & 200 EMA",
                    isSelected = selectedPreset == "sol_12h" && selectedImageUri == null,
                    onClick = { viewModel.selectPreset("sol_12h") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chart Preview or Upload Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black)
                    .border(1.dp, QuantBorder, RoundedCornerShape(10.dp))
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Uploaded Chart Screenshot",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    val presetDrawable = when (selectedPreset) {
                        "eth_1h" -> R.drawable.chart_eth_1h
                        "sol_12h" -> R.drawable.chart_sol_12h
                        else -> R.drawable.chart_btc_4h
                    }
                    Image(
                        painter = painterResource(id = presetDrawable),
                        contentDescription = "Sample Chart",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Overlay badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .border(1.dp, QuantBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Upload",
                            tint = QuantCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (selectedImageUri != null) "Custom Screenshot (Tap to change)" else "Tap to Upload Own Chart",
                            color = QuantTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Parameters Card (Pair, Timeframe, Account Balance, Risk %)
        QuantCard {
            Text(
                text = "2. ASSET PAIR & TIMEFRAME",
                color = QuantCyan,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = assetPair,
                    onValueChange = { viewModel.setAssetPair(it) },
                    label = { Text("Asset Pair") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QuantCyan,
                        unfocusedBorderColor = QuantBorder,
                        focusedTextColor = QuantTextPrimary,
                        unfocusedTextColor = QuantTextPrimary
                    ),
                    modifier = Modifier.weight(1.3f).testTag("asset_pair_input")
                )

                // Timeframe Chips (1h, 4h, 12h)
                Column(modifier = Modifier.weight(1.7f)) {
                    Text("Timeframe", fontSize = 11.sp, color = QuantTextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("1h", "4h", "12h").forEach { tf ->
                            FilterChip(
                                selected = timeframe == tf,
                                onClick = { viewModel.setTimeframe(tf) },
                                label = { Text(tf, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = QuantCyan,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = QuantBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "3. QUANTITATIVE RISK PARAMETERS",
                color = QuantCyan,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = if (accountBalance == 0.0) "" else accountBalance.toInt().toString(),
                    onValueChange = { str ->
                        val parsed = str.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                        viewModel.setAccountBalance(parsed)
                    },
                    label = { Text("Capital ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QuantCyan,
                        unfocusedBorderColor = QuantBorder,
                        focusedTextColor = QuantTextPrimary,
                        unfocusedTextColor = QuantTextPrimary
                    ),
                    modifier = Modifier.weight(1.2f).testTag("account_balance_input")
                )

                OutlinedTextField(
                    value = riskPercentage.toString(),
                    onValueChange = { str ->
                        val parsed = str.toDoubleOrNull() ?: 1.0
                        viewModel.setRiskPercentage(parsed)
                    },
                    label = { Text("Risk %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QuantCyan,
                        unfocusedBorderColor = QuantBorder,
                        focusedTextColor = QuantTextPrimary,
                        unfocusedTextColor = QuantTextPrimary
                    ),
                    modifier = Modifier.weight(0.8f).testTag("risk_pct_input")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Preset Risk Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(0.5, 1.0, 2.0, 3.0, 5.0).forEach { pct ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (riskPercentage == pct) QuantAmber.copy(alpha = 0.25f) else QuantDarkSurfaceElevated)
                            .border(
                                1.dp,
                                if (riskPercentage == pct) QuantAmber else QuantBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { viewModel.setRiskPercentage(pct) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$pct%",
                            color = if (riskPercentage == pct) QuantAmber else QuantTextSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            val currentRiskDollar = accountBalance * (riskPercentage / 100.0)
            Text(
                text = String.format(Locale.US, "Calculated Max Loss per Trade: $%,.2f", currentRiskDollar),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = QuantAmber
            )
        }

        // Action Trigger Button
        Button(
            onClick = { viewModel.runChartAnalysis() },
            colors = ButtonDefaults.buttonColors(containerColor = QuantGreen),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("run_analysis_button")
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "RUN QUANT ANALYSIS",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Analysis State Processing or Results
        when (val state = analysisState) {
            is AnalysisUiState.Idle -> {
                // Initial prompt tip
                QuantCard(borderColor = QuantBorder.copy(alpha = 0.5f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = QuantAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Quantitative Risk Protocol",
                                fontWeight = FontWeight.Bold,
                                color = QuantTextPrimary,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Prioritizes capital preservation. If technical signals conflict across 1h, 4h, and 12h timeframes, the engine defaults to HOLD / WAIT.",
                                color = QuantTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            is AnalysisUiState.Analyzing -> {
                QuantCard(borderColor = QuantCyan) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = QuantCyan, modifier = Modifier.size(40.dp))
                        Text(
                            text = "ANALYZING CANDLESTICK CHART...",
                            color = QuantCyan,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.step,
                            color = QuantTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            is AnalysisUiState.Error -> {
                QuantCard(borderColor = QuantRed) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = QuantRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "Analysis Error",
                                color = QuantRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = state.message,
                                color = QuantTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            is AnalysisUiState.Success -> {
                // The Full 6-Section Technical Breakdown
                AnalysisReportView(
                    signal = state.signal,
                    onSave = { viewModel.saveCurrentSignal(state.signal) },
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Trade Signal", state.signal.fullReport)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Full technical report copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun PresetChip(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) QuantDarkSurfaceElevated else QuantDarkSurface)
            .border(
                1.dp,
                if (isSelected) QuantCyan else QuantBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) QuantCyan else QuantTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = QuantTextMuted
            )
        }
    }
}

@Composable
fun AnalysisReportView(
    signal: TradeSignal,
    onSave: () -> Unit,
    onCopy: () -> Unit
) {
    var saved by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Section 3 Headline: Actionable Signal
        QuantCard(
            borderColor = when (signal.recommendation) {
                SignalType.BUY -> QuantGreen
                SignalType.SELL -> QuantRed
                SignalType.HOLD -> QuantAmber
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "3. ACTIONABLE SIGNAL",
                        fontSize = 11.sp,
                        color = QuantCyan,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = signal.setupType,
                        style = MaterialTheme.typography.titleMedium,
                        color = QuantTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                SignalBadge(
                    signalType = signal.recommendation,
                    confidence = signal.confidence
                )
            }
        }

        // Section 1: Market Overview & Timeframe Context
        QuantCard {
            SectionHeader(
                number = "1",
                title = "Market Overview & Timeframe",
                badgeText = "${signal.assetPair} • ${signal.timeframe}",
                badgeColor = QuantCyan
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OverviewRow(
                    label = "Macro Trend (200 EMA):",
                    value = signal.macroTrend,
                    valueColor = if (signal.macroTrend.contains("Bullish", ignoreCase = true)) QuantGreen else QuantAmber
                )
                OverviewRow(
                    label = "Micro Momentum (1h RSI):",
                    value = signal.microMomentum,
                    valueColor = if (signal.microMomentum.contains("Bearish", ignoreCase = true)) QuantRed else QuantTextPrimary
                )
            }
        }

        // Section 2: Technical Indicator Breakdown
        QuantCard {
            SectionHeader(
                number = "2",
                title = "Technical Indicator Breakdown",
                badgeText = "20/50/200 EMA + RSI + VOL",
                badgeColor = QuantLime
            )
            Spacer(modifier = Modifier.height(10.dp))

            IndicatorDetailCard(
                indicator = "MOVING AVERAGES (20 / 50 / 200 EMA)",
                description = signal.movingAveragesSummary,
                accentColor = QuantCyan
            )
            Spacer(modifier = Modifier.height(8.dp))

            IndicatorDetailCard(
                indicator = "RSI & DIVERGENCE ANALYSIS",
                description = signal.rsiDivergenceSummary,
                accentColor = QuantLime
            )
            Spacer(modifier = Modifier.height(8.dp))

            IndicatorDetailCard(
                indicator = "VOLUME & VOLUME PROFILE (HVN / LVN)",
                description = signal.volumeProfileSummary,
                accentColor = QuantAmber
            )
        }

        // Section 4: Precision Execution Parameters (Safe Price Model)
        QuantCard(borderColor = QuantGreen.copy(alpha = 0.5f)) {
            SectionHeader(
                number = "4",
                title = "Precision Execution Parameters",
                badgeText = String.format(Locale.US, "RRR 1:%.2f", signal.rrr),
                badgeColor = if (signal.rrr >= 2.0) QuantGreen else QuantAmber
            )
            Spacer(modifier = Modifier.height(10.dp))

            PriceLevelRow(
                label = "Take-Profit Target 2 (Extended)",
                price = signal.takeProfit2,
                subtext = "LVN gap & expansion swing high",
                accentColor = QuantGreen
            )
            PriceLevelRow(
                label = "Take-Profit Target 1 (Conservative)",
                price = signal.takeProfit1,
                subtext = "Nearest resistance / HVN block",
                accentColor = QuantLime
            )
            PriceLevelRow(
                label = "Safe Entry Price Zone",
                price = (signal.safeEntryMin + signal.safeEntryMax) / 2.0,
                subtext = String.format(Locale.US, "Range: $%,.2f – $%,.2f", signal.safeEntryMin, signal.safeEntryMax),
                accentColor = QuantCyan
            )
            PriceLevelRow(
                label = "Stop-Loss (Risk Control)",
                price = signal.stopLoss,
                subtext = "Placement below structural support / EMA",
                accentColor = QuantRed
            )
        }

        // Section 5: Position Sizing & Money Management (Exact math)
        val posRisk = PositionRiskResult.calculate(
            accountBalance = signal.accountBalance,
            riskPercentage = signal.riskPercentage,
            entryPrice = (signal.safeEntryMin + signal.safeEntryMax) / 2.0,
            stopLossPrice = signal.stopLoss,
            takeProfit1 = signal.takeProfit1,
            takeProfit2 = signal.takeProfit2,
            leverage = 1
        )
        PositionSizingCard(result = posRisk)

        // Section 6: Invalidation & Risk Conditions
        QuantCard(borderColor = QuantRedDark) {
            SectionHeader(
                number = "6",
                title = "Invalidation & Risk Conditions",
                badgeText = "STRICT INVOCATION",
                badgeColor = QuantRed
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(QuantRedDark.copy(alpha = 0.3f))
                    .border(1.dp, QuantRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "STRUCTURAL INVALIDATION LEVEL",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = QuantRedGlow
                    )
                    Text(
                        text = String.format(Locale.US, "$%,.2f", signal.invalidationLevel),
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Any candle close crossing this price invalidates the entire trade thesis immediately.",
                        fontSize = 11.sp,
                        color = QuantTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Key Operational Risks:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = QuantTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            signal.keyRisks.forEach { risk ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("• ", color = QuantAmber, fontWeight = FontWeight.Bold)
                    Text(text = risk, fontSize = 12.sp, color = QuantTextSecondary)
                }
            }
        }

        // Action Buttons: Save to Trade Journal & Copy Report
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onCopy,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).testTag("copy_report_button")
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Report", fontSize = 13.sp)
            }

            Button(
                onClick = {
                    onSave()
                    saved = true
                },
                enabled = !saved,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (saved) QuantDarkSurfaceElevated else QuantGreen
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1.2f).testTag("save_signal_button")
            ) {
                Icon(
                    imageVector = if (saved) Icons.Default.Check else Icons.Default.BookmarkAdd,
                    contentDescription = null,
                    tint = if (saved) QuantGreen else Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (saved) "Saved to Journal" else "Save to Journal",
                    color = if (saved) QuantGreen else Color.Black,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OverviewRow(
    label: String,
    value: String,
    valueColor: Color
) {
    Column {
        Text(text = label, fontSize = 11.sp, color = QuantTextMuted, fontFamily = FontFamily.Monospace)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = valueColor)
    }
}

@Composable
fun IndicatorDetailCard(
    indicator: String,
    description: String,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(QuantDarkSurfaceElevated)
            .border(1.dp, QuantBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = indicator,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = QuantTextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}
