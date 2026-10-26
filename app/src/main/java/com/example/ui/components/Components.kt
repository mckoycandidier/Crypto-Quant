package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PositionRiskResult
import com.example.data.model.SignalConfidence
import com.example.data.model.SignalType
import com.example.ui.theme.QuantAmber
import com.example.ui.theme.QuantBorder
import com.example.ui.theme.QuantCyan
import com.example.ui.theme.QuantDarkSurface
import com.example.ui.theme.QuantDarkSurfaceElevated
import com.example.ui.theme.QuantGreen
import com.example.ui.theme.QuantGreenDark
import com.example.ui.theme.QuantLime
import com.example.ui.theme.QuantRed
import com.example.ui.theme.QuantRedDark
import com.example.ui.theme.QuantRedGlow
import com.example.ui.theme.QuantRedDark
import com.example.ui.theme.QuantTextMuted
import com.example.ui.theme.QuantTextPrimary
import com.example.ui.theme.QuantTextSecondary
import java.util.Locale

@Composable
fun SignalBadge(
    signalType: SignalType,
    confidence: SignalConfidence,
    modifier: Modifier = Modifier
) {
    val (bg, border, text, icon) = when (signalType) {
        SignalType.BUY -> Quad(QuantGreenDark, QuantGreen, QuantGreen, Icons.Default.ArrowUpward)
        SignalType.SELL -> Quad(QuantRedDark, QuantRed, QuantRed, Icons.Default.ArrowDownward)
        SignalType.HOLD -> Quad(Color(0xFF332A00), QuantAmber, QuantAmber, Icons.Default.Pause)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg.copy(alpha = 0.8f))
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = text,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = signalType.name,
            color = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = "• ${confidence.name}",
            color = text.copy(alpha = 0.8f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun SectionHeader(
    number: String,
    title: String,
    badgeText: String? = null,
    badgeColor: Color = QuantCyan,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = QuantTextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(badgeColor.copy(alpha = 0.2f))
                    .border(1.dp, badgeColor.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun QuantCard(
    modifier: Modifier = Modifier,
    borderColor: Color = QuantBorder,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = QuantDarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            content()
        }
    }
}

@Composable
fun PriceLevelRow(
    label: String,
    price: Double,
    subtext: String? = null,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = QuantTextPrimary
                )
                if (subtext != null) {
                    Text(
                        text = subtext,
                        fontSize = 11.sp,
                        color = QuantTextMuted
                    )
                }
            }
        }
        Text(
            text = String.format(Locale.US, "$%,.2f", price),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = accentColor
        )
    }
}

@Composable
fun PositionSizingCard(
    result: PositionRiskResult,
    modifier: Modifier = Modifier
) {
    QuantCard(
        modifier = modifier.testTag("position_sizing_card"),
        borderColor = QuantCyan.copy(alpha = 0.6f)
    ) {
        SectionHeader(
            number = "5",
            title = "Position Sizing & Money Management",
            badgeText = "EXACT MATH",
            badgeColor = QuantCyan
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Capital & Risk row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(QuantDarkSurfaceElevated)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("ACCOUNT CAPITAL", fontSize = 10.sp, color = QuantTextMuted, fontFamily = FontFamily.Monospace)
                Text(
                    String.format(Locale.US, "$%,.2f", result.accountBalance),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = QuantTextPrimary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("RISK PER TRADE", fontSize = 10.sp, color = QuantTextMuted, fontFamily = FontFamily.Monospace)
                Text(
                    String.format(Locale.US, "%.1f%%  →  $%,.2f", result.riskPercentage, result.maxDollarRisk),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = QuantAmber
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Key computed metrics grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricBox(
                label = "SL DISTANCE",
                value = String.format(Locale.US, "%.2f%%", result.stopLossDistancePercent),
                valueColor = QuantRed,
                modifier = Modifier.weight(1f)
            )
            MetricBox(
                label = "MAX RISK ($)",
                value = String.format(Locale.US, "$%,.2f", result.maxDollarRisk),
                valueColor = QuantRedGlow,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricBox(
                label = "POSITION UNITS",
                value = String.format(Locale.US, "%,.4f", result.positionSizeUnits),
                valueColor = QuantGreen,
                modifier = Modifier.weight(1f)
            )
            MetricBox(
                label = "NOTIONAL VALUE",
                value = String.format(Locale.US, "$%,.2f", result.totalPositionValue),
                valueColor = QuantCyan,
                modifier = Modifier.weight(1f)
            )
        }

        if (result.leverage > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Margin Required (${result.leverage}x Leverage):",
                    fontSize = 12.sp,
                    color = QuantTextSecondary
                )
                Text(
                    text = String.format(Locale.US, "$%,.2f", result.requiredMargin),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = QuantLime
                )
            }
        }
    }
}

@Composable
fun MetricBox(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(QuantDarkSurfaceElevated)
            .border(1.dp, QuantBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = QuantTextMuted,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}
