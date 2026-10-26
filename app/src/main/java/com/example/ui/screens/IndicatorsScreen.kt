package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.MultilineChart
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QuantCard
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

@Composable
fun IndicatorsScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuantDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        QuantCard(borderColor = QuantCyan.copy(alpha = 0.4f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TECHNICAL METHODOLOGY",
                        color = QuantCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Indicator Rulebook",
                        color = QuantTextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Strict analysis criteria for EMAs, RSI Divergences, & Volume Profile",
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
                        imageVector = Icons.AutoMirrored.Filled.MultilineChart,
                        contentDescription = "Indicators",
                        tint = QuantCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Section 1: 20 / 50 / 200 EMA Rules
        QuantCard(borderColor = QuantCyan.copy(alpha = 0.5f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, tint = QuantCyan)
                Text(
                    text = "1. EMA Trend & Dynamic Support/Resistance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = QuantTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            RuleBlock(
                title = "200 EMA (Macro Trend Filter)",
                rules = listOf(
                    "Price ABOVE 200 EMA = Macro Bullish Bias (Only long/buy setups preferred).",
                    "Price BELOW 200 EMA = Macro Bearish Bias (Only short/sell setups preferred)."
                ),
                color = QuantAmber
            )

            Spacer(modifier = Modifier.height(8.dp))

            RuleBlock(
                title = "20 / 50 EMA Alignment & Crossovers",
                rules = listOf(
                    "Golden Cross: 20/50 EMA crossing above 200 EMA → Strong bullish momentum signal.",
                    "Death Cross: 20/50 EMA crossing below 200 EMA → Strong bearish trend warning.",
                    "EMA Compression / Squeeze: 20, 50, and 200 EMAs converge tightly → High-volatility expansion imminent."
                ),
                color = QuantCyan
            )
        }

        // Section 2: RSI & Divergence Rules
        QuantCard(borderColor = QuantLime.copy(alpha = 0.5f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.MultilineChart, contentDescription = null, tint = QuantLime)
                Text(
                    text = "2. RSI & Divergence Detection Rules",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = QuantTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            RuleBlock(
                title = "Standard Thresholds",
                rules = listOf(
                    "Overbought: > 70 | Oversold: < 30",
                    "Bullish Control Zone: 50 – 70 (trend continuation)",
                    "Bearish Control Zone: 30 – 50 (selling dominance)"
                ),
                color = QuantLime
            )

            Spacer(modifier = Modifier.height(8.dp))

            RuleBlock(
                title = "Divergence Classifications",
                rules = listOf(
                    "Regular Bullish Divergence: Price Lower Low (LL) + RSI Higher Low (HL) → Trend Reversal / Buy Entry.",
                    "Regular Bearish Divergence: Price Higher High (HH) + RSI Lower High (LH) → Rejection / Sell Target.",
                    "Hidden Bullish Divergence: Price Higher Low (HL) + RSI Lower Low (LL) → Trend Continuation (Buy on dip)."
                ),
                color = QuantGreen
            )
        }

        // Section 3: Volume & Volume Profile
        QuantCard(borderColor = QuantAmber.copy(alpha = 0.5f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.BarChart, contentDescription = null, tint = QuantAmber)
                Text(
                    text = "3. Volume & Volume Profile Rules",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = QuantTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            RuleBlock(
                title = "Volume Bar Confirmation",
                rules = listOf(
                    "Breakouts or breakdowns MUST be accompanied by a noticeable volume spike (>20-period average).",
                    "Low-volume moves are treated as fakeouts and traps."
                ),
                color = QuantAmber
            )

            Spacer(modifier = Modifier.height(8.dp))

            RuleBlock(
                title = "Volume Profile Structural Nodes",
                rules = listOf(
                    "High Volume Nodes (HVN / POC): Dense volume blocks act as strong magnetic support/resistance. Prefer entries near HVN support.",
                    "Low Volume Nodes (LVN / Gaps): Price moves rapidly through low-volume areas. Use LVNs for target projections, not entries."
                ),
                color = QuantCyan
            )
        }
    }
}

@Composable
fun RuleBlock(
    title: String,
    rules: List<String>,
    color: Color
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
                text = title,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(6.dp))
            rules.forEach { rule ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("• ", color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = rule, fontSize = 12.sp, color = QuantTextSecondary, lineHeight = 17.sp)
                }
            }
        }
    }
}
