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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MetricBox
import com.example.ui.components.PositionSizingCard
import com.example.ui.components.QuantCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.QuantAmber
import com.example.ui.theme.QuantBorder
import com.example.ui.theme.QuantCyan
import com.example.ui.theme.QuantDarkBackground
import com.example.ui.theme.QuantDarkSurface
import com.example.ui.theme.QuantDarkSurfaceElevated
import com.example.ui.theme.QuantGreen
import com.example.ui.theme.QuantLime
import com.example.ui.theme.QuantRed
import com.example.ui.theme.QuantRedGlow
import com.example.ui.theme.QuantTextMuted
import com.example.ui.theme.QuantTextPrimary
import com.example.ui.theme.QuantTextSecondary
import com.example.ui.viewmodel.QuantViewModel
import java.util.Locale

@Composable
fun RiskCalculatorScreen(
    viewModel: QuantViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val balance by viewModel.calcBalance.collectAsState()
    val riskPct by viewModel.calcRiskPct.collectAsState()
    val entry by viewModel.calcEntry.collectAsState()
    val sl by viewModel.calcSl.collectAsState()
    val tp1 by viewModel.calcTp1.collectAsState()
    val tp2 by viewModel.calcTp2.collectAsState()
    val leverage by viewModel.calcLeverage.collectAsState()
    val result by viewModel.calcResult.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuantDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Card
        QuantCard(borderColor = QuantCyan.copy(alpha = 0.4f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "QUANT RISK ENGINE",
                        color = QuantCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Position Sizing Calculator",
                        color = QuantTextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Exact mathematical risk-per-trade & capital preservation formulas",
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
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Calculator",
                        tint = QuantCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Live Mathematical Formulas Reference
        QuantCard {
            Text(
                text = "DISCIPLINED RISK FORMULAS (EXACT RULES)",
                color = QuantCyan,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            FormulaSnippet(
                ruleNumber = "1",
                name = "Max Dollar Risk ($)",
                formula = "Account Balance × (Risk % / 100)",
                liveValue = String.format(Locale.US, "$%,.2f", result.maxDollarRisk),
                valueColor = QuantRedGlow
            )
            Spacer(modifier = Modifier.height(6.dp))
            FormulaSnippet(
                ruleNumber = "2",
                name = "Stop-Loss Distance (%)",
                formula = "(|Entry - SL| / Entry) × 100",
                liveValue = String.format(Locale.US, "%.2f%%", result.stopLossDistancePercent),
                valueColor = QuantAmber
            )
            Spacer(modifier = Modifier.height(6.dp))
            FormulaSnippet(
                ruleNumber = "3",
                name = "Position Size (Units)",
                formula = "Max Risk ($) / |Entry - SL|",
                liveValue = String.format(Locale.US, "%,.4f units", result.positionSizeUnits),
                valueColor = QuantGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            FormulaSnippet(
                ruleNumber = "4",
                name = "Total Position Value ($)",
                formula = "Position Size (Units) × Entry Price",
                liveValue = String.format(Locale.US, "$%,.2f Notional", result.totalPositionValue),
                valueColor = QuantCyan
            )
        }

        // User Inputs Card
        QuantCard {
            Text(
                text = "EXECUTION & CAPITAL INPUTS",
                color = QuantCyan,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Capital and Risk %
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = if (balance == 0.0) "" else balance.toInt().toString(),
                    onValueChange = { str ->
                        val parsed = str.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                        viewModel.updateCalcParams(balance = parsed)
                    },
                    label = { Text("Account Balance ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QuantCyan,
                        unfocusedBorderColor = QuantBorder,
                        focusedTextColor = QuantTextPrimary,
                        unfocusedTextColor = QuantTextPrimary
                    ),
                    modifier = Modifier.weight(1.2f).testTag("calc_balance_input")
                )

                OutlinedTextField(
                    value = riskPct.toString(),
                    onValueChange = { str ->
                        val parsed = str.toDoubleOrNull() ?: 1.0
                        viewModel.updateCalcParams(riskPct = parsed)
                    },
                    label = { Text("Risk %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QuantCyan,
                        unfocusedBorderColor = QuantBorder,
                        focusedTextColor = QuantTextPrimary,
                        unfocusedTextColor = QuantTextPrimary
                    ),
                    modifier = Modifier.weight(0.8f).testTag("calc_risk_input")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Risk % quick chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(0.5, 1.0, 2.0, 3.0, 5.0).forEach { p ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (riskPct == p) QuantAmber.copy(alpha = 0.25f) else QuantDarkSurfaceElevated)
                            .border(1.dp, if (riskPct == p) QuantAmber else QuantBorder, RoundedCornerShape(6.dp))
                            .clickable { viewModel.updateCalcParams(riskPct = p) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$p%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (riskPct == p) QuantAmber else QuantTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = QuantBorder)
            Spacer(modifier = Modifier.height(14.dp))

            // Entry and Stop Loss
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = entry.toString(),
                    onValueChange = { str ->
                        val parsed = str.toDoubleOrNull() ?: 0.0
                        viewModel.updateCalcParams(entry = parsed)
                    },
                    label = { Text("Entry Price ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QuantCyan,
                        unfocusedBorderColor = QuantBorder,
                        focusedTextColor = QuantTextPrimary,
                        unfocusedTextColor = QuantTextPrimary
                    ),
                    modifier = Modifier.weight(1f).testTag("calc_entry_input")
                )

                OutlinedTextField(
                    value = sl.toString(),
                    onValueChange = { str ->
                        val parsed = str.toDoubleOrNull() ?: 0.0
                        viewModel.updateCalcParams(sl = parsed)
                    },
                    label = { Text("Stop Loss ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QuantRed,
                        unfocusedBorderColor = QuantBorder,
                        focusedTextColor = QuantTextPrimary,
                        unfocusedTextColor = QuantTextPrimary
                    ),
                    modifier = Modifier.weight(1f).testTag("calc_sl_input")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Take Profit 1 & Take Profit 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = tp1.toString(),
                    onValueChange = { str ->
                        val parsed = str.toDoubleOrNull() ?: 0.0
                        viewModel.updateCalcParams(tp1 = parsed)
                    },
                    label = { Text("Take Profit 1 ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QuantLime,
                        unfocusedBorderColor = QuantBorder,
                        focusedTextColor = QuantTextPrimary,
                        unfocusedTextColor = QuantTextPrimary
                    ),
                    modifier = Modifier.weight(1f).testTag("calc_tp1_input")
                )

                OutlinedTextField(
                    value = tp2.toString(),
                    onValueChange = { str ->
                        val parsed = str.toDoubleOrNull() ?: 0.0
                        viewModel.updateCalcParams(tp2 = parsed)
                    },
                    label = { Text("Take Profit 2 ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QuantGreen,
                        unfocusedBorderColor = QuantBorder,
                        focusedTextColor = QuantTextPrimary,
                        unfocusedTextColor = QuantTextPrimary
                    ),
                    modifier = Modifier.weight(1f).testTag("calc_tp2_input")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Leverage Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Leverage", fontSize = 12.sp, color = QuantTextSecondary)
                    Text(
                        text = "${leverage}x ${if (leverage == 1) "(Spot 1:1)" else "(Futures)"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (leverage == 1) QuantGreen else QuantCyan
                    )
                }
                Slider(
                    value = leverage.toFloat(),
                    onValueChange = { viewModel.updateCalcParams(leverage = it.toInt()) },
                    valueRange = 1f..50f,
                    steps = 49,
                    colors = SliderDefaults.colors(
                        thumbColor = QuantCyan,
                        activeTrackColor = QuantCyan,
                        inactiveTrackColor = QuantDarkSurfaceElevated
                    ),
                    modifier = Modifier.testTag("leverage_slider")
                )
            }
        }

        // RRR Compliance Check & Profit Projections
        QuantCard(
            borderColor = if (result.isRrrValid) QuantGreen else QuantAmber
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (result.isRrrValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (result.isRrrValid) QuantGreen else QuantAmber,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (result.isRrrValid) "RISK-TO-REWARD VALID (>= 1:2)" else "RRR WARNING: BELOW 1:2 THRESHOLD",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (result.isRrrValid) QuantGreen else QuantAmber
                    )
                    Text(
                        text = String.format(Locale.US, "Target 1 RRR: 1:%.2f  •  Target 2 RRR: 1:%.2f", result.rrrTarget1, result.rrrTarget2),
                        fontSize = 13.sp,
                        color = QuantTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = QuantBorder)
            Spacer(modifier = Modifier.height(12.dp))

            // Profit projections
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricBox(
                    label = "TP1 PROFIT ($)",
                    value = String.format(Locale.US, "+$%,.2f", result.potentialProfitTarget1),
                    valueColor = QuantLime,
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    label = "TP2 PROFIT ($)",
                    value = String.format(Locale.US, "+$%,.2f", result.potentialProfitTarget2),
                    valueColor = QuantGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Exact Position Sizing Output Card
        PositionSizingCard(result = result)
    }
}

@Composable
fun FormulaSnippet(
    ruleNumber: String,
    name: String,
    formula: String,
    liveValue: String,
    valueColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(QuantDarkSurfaceElevated)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$ruleNumber. $name",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuantTextPrimary
                )
                Text(
                    text = formula,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = QuantTextMuted
                )
            }
            Text(
                text = liveValue,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = valueColor
            )
        }
    }
}
