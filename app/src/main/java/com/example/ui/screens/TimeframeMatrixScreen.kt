package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridGoldenratio
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SignalConfidence
import com.example.data.model.SignalType
import com.example.ui.components.QuantCard
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
import com.example.ui.theme.QuantTextMuted
import com.example.ui.theme.QuantTextPrimary
import com.example.ui.theme.QuantTextSecondary
import com.example.ui.viewmodel.QuantViewModel

@Composable
fun TimeframeMatrixScreen(
    viewModel: QuantViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val matrix12h by viewModel.matrix12h.collectAsState()
    val matrix4h by viewModel.matrix4h.collectAsState()
    val matrix1h by viewModel.matrix1h.collectAsState()

    // Confluence logic:
    // 12h: Bullish / Bearish / Ranging
    // 4h: Bullish / Bearish / Neutral
    // 1h: Bullish / Bearish / Divergence / Neutral
    val is12hBullish = matrix12h.contains("Bullish")
    val is12hBearish = matrix12h.contains("Bearish")

    val is4hBullish = matrix4h.contains("Bullish")
    val is4hBearish = matrix4h.contains("Bearish")

    val is1hBullish = matrix1h.contains("Bullish")
    val is1hBearish = matrix1h.contains("Bearish")

    val hasConflict = (is12hBullish && is1hBearish) || (is12hBearish && is1hBullish) || (is4hBullish && is1hBearish) || (is4hBearish && is1hBullish)

    val verdictSignal = when {
        hasConflict -> SignalType.HOLD
        is12hBullish && is4hBullish && is1hBullish -> SignalType.BUY
        is12hBearish && is4hBearish && is1hBearish -> SignalType.SELL
        else -> SignalType.HOLD
    }

    val verdictConfidence = when {
        hasConflict -> SignalConfidence.LOW
        verdictSignal == SignalType.BUY || verdictSignal == SignalType.SELL -> SignalConfidence.HIGH
        else -> SignalConfidence.MEDIUM
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuantDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        QuantCard(borderColor = QuantCyan.copy(alpha = 0.4f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DISCIPLINED ALIGNMENT",
                        color = QuantCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Timeframe Alignment Matrix",
                        color = QuantTextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "12h Macro Trend + 4h S/R Structure + 1h Precision Momentum",
                        color = QuantTextSecondary,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(QuantDarkSurfaceElevated)
                        .border(1.dp, QuantCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GridGoldenratio,
                        contentDescription = "Matrix",
                        tint = QuantCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Live Confluence Verdict
        QuantCard(
            borderColor = when (verdictSignal) {
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
                        text = "MULTI-TIMEFRAME VERDICT",
                        fontSize = 11.sp,
                        color = QuantCyan,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (hasConflict) "CONFLICT DETECTED → CAPITAL PRESERVATION" else "HIGH CONFLUENCE CONFIRMED",
                        fontSize = 14.sp,
                        color = if (hasConflict) QuantAmber else QuantGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                SignalBadge(signalType = verdictSignal, confidence = verdictConfidence)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = QuantBorder)
            Spacer(modifier = Modifier.height(10.dp))

            if (hasConflict) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = QuantAmber, modifier = Modifier.size(20.dp))
                    Text(
                        text = "RULE #1 IN EFFECT: Technical signals conflict across timeframes. Per risk mandate, system defaults to HOLD / WAIT.",
                        fontSize = 12.sp,
                        color = QuantAmber,
                        lineHeight = 16.sp
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = QuantGreen, modifier = Modifier.size(20.dp))
                    Text(
                        text = "All 3 timeframes are aligned in direction. Safe entry permitted near EMA dynamic support / HVN node.",
                        fontSize = 12.sp,
                        color = QuantGreen,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Tier 1: 12-Hour Macro Timeframe
        MatrixTierCard(
            tier = "TIER 1 (MACRO)",
            timeframe = "12 Hours (12h)",
            purpose = "Sets macro direction relative to 200 EMA & major Volume Profile POC nodes.",
            options = listOf(
                "Above 200 EMA (Bullish)",
                "Below 200 EMA (Bearish)",
                "Trapped in Squeeze (Ranging)"
            ),
            selectedOption = matrix12h,
            onSelect = { viewModel.setMatrix12h(it) },
            accentColor = QuantAmber
        )

        // Tier 2: 4-Hour Market Structure
        MatrixTierCard(
            tier = "TIER 2 (STRUCTURE)",
            timeframe = "4 Hours (4h)",
            purpose = "Identifies EMA alignment (20/50), S/R zones, and chart swing patterns.",
            options = listOf(
                "Bullish EMA 20/50 Alignment",
                "Bearish EMA 20/50 Death Cross",
                "Testing Dynamic Support / Squeeze"
            ),
            selectedOption = matrix4h,
            onSelect = { viewModel.setMatrix4h(it) },
            accentColor = QuantLime
        )

        // Tier 3: 1-Hour Micro Execution
        MatrixTierCard(
            tier = "TIER 3 (MOMENTUM & EXECUTION)",
            timeframe = "1 Hour (1h)",
            purpose = "Scans RSI divergences and volume spikes for precision entry and stop-loss placement.",
            options = listOf(
                "Bullish Volume Bounce (No Div)",
                "Active Regular Bearish Divergence (Sell/Exit)",
                "Regular Bullish Divergence (Buy Entry)",
                "Neutral / Low Volume Fakeout Risk"
            ),
            selectedOption = matrix1h,
            onSelect = { viewModel.setMatrix1h(it) },
            accentColor = QuantCyan
        )
    }
}

@Composable
fun MatrixTierCard(
    tier: String,
    timeframe: String,
    purpose: String,
    options: List<String>,
    selectedOption: String,
    onSelect: (String) -> Unit,
    accentColor: Color
) {
    QuantCard(borderColor = accentColor.copy(alpha = 0.5f)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = tier,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Text(
                    text = timeframe,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = QuantTextPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = purpose, fontSize = 11.sp, color = QuantTextSecondary)

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEach { opt ->
                val isSelected = opt == selectedOption
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) accentColor.copy(alpha = 0.15f) else QuantDarkSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) accentColor else QuantBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelect(opt) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opt,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) accentColor else QuantTextPrimary
                        )
                        if (isSelected) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }
                    }
                }
            }
        }
    }
}
