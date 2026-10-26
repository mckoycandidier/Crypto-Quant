package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TradeSignal
import com.example.ui.components.PriceLevelRow
import com.example.ui.components.QuantCard
import com.example.ui.components.SignalBadge
import com.example.ui.theme.QuantAmber
import com.example.ui.theme.QuantBorder
import com.example.ui.theme.QuantCyan
import com.example.ui.theme.QuantDarkBackground
import com.example.ui.theme.QuantDarkSurfaceElevated
import com.example.ui.theme.QuantGreen
import com.example.ui.theme.QuantLime
import com.example.ui.theme.QuantRed
import com.example.ui.theme.QuantTextMuted
import com.example.ui.theme.QuantTextPrimary
import com.example.ui.theme.QuantTextSecondary
import com.example.ui.viewmodel.QuantViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalScreen(
    viewModel: QuantViewModel,
    modifier: Modifier = Modifier
) {
    val signals by viewModel.savedSignals.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredSignals = when (selectedFilter) {
        "All" -> signals
        else -> signals.filter { it.status.equals(selectedFilter, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuantDarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        QuantCard(borderColor = QuantCyan.copy(alpha = 0.4f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DISCIPLINED EXECUTION",
                        color = QuantCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Trade Signals Journal",
                        color = QuantTextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${signals.size} trade setups saved with quantitative risk parameters",
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
                        imageVector = Icons.Default.Book,
                        contentDescription = "Journal",
                        tint = QuantCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Status filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Active", "Hit TP1", "Hit TP2", "Stopped Out", "Invalidated").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = QuantCyan,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        if (filteredSignals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = QuantTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No trade signals in journal",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuantTextSecondary
                    )
                    Text(
                        text = "Run a chart analysis in the 'Analyze' tab and save your first setup.",
                        fontSize = 12.sp,
                        color = QuantTextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredSignals, key = { it.id }) { signal ->
                    JournalItemCard(
                        signal = signal,
                        onStatusChange = { newStatus -> viewModel.updateSignalStatus(signal.id, newStatus) },
                        onDelete = { viewModel.deleteSignal(signal) }
                    )
                }
            }
        }
    }
}

@Composable
fun JournalItemCard(
    signal: TradeSignal,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }

    val dateFormatted = remember(signal.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        sdf.format(Date(signal.timestamp))
    }

    QuantCard(
        borderColor = when (signal.status) {
            "Hit TP1", "Hit TP2" -> QuantGreen
            "Stopped Out" -> QuantRed
            "Invalidated" -> QuantAmber
            else -> QuantBorder
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = signal.assetPair,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuantTextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(QuantDarkSurfaceElevated)
                            .border(1.dp, QuantBorder, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = signal.timeframe,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = QuantCyan
                        )
                    }
                }
                Text(
                    text = dateFormatted,
                    fontSize = 11.sp,
                    color = QuantTextMuted
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                SignalBadge(signalType = signal.recommendation, confidence = signal.confidence)
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = QuantTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Key stats row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(QuantDarkSurfaceElevated)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("ENTRY", fontSize = 9.sp, color = QuantTextMuted, fontFamily = FontFamily.Monospace)
                Text(
                    String.format(Locale.US, "$%,.2f", (signal.safeEntryMin + signal.safeEntryMax) / 2.0),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = QuantCyan
                )
            }
            Column {
                Text("STOP LOSS", fontSize = 9.sp, color = QuantTextMuted, fontFamily = FontFamily.Monospace)
                Text(
                    String.format(Locale.US, "$%,.2f", signal.stopLoss),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = QuantRed
                )
            }
            Column {
                Text("TP1 / TP2", fontSize = 9.sp, color = QuantTextMuted, fontFamily = FontFamily.Monospace)
                Text(
                    String.format(Locale.US, "$%,.2f", signal.takeProfit1),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = QuantGreen
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("RRR", fontSize = 9.sp, color = QuantTextMuted, fontFamily = FontFamily.Monospace)
                Text(
                    String.format(Locale.US, "1:%.2f", signal.rrr),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (signal.rrr >= 2.0) QuantGreen else QuantAmber
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status badge & Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(QuantDarkSurfaceElevated)
                    .border(1.dp, QuantBorder, RoundedCornerShape(6.dp))
                    .clickable { showStatusDialog = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Status: ${signal.status} ▾",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (signal.status) {
                        "Hit TP1", "Hit TP2" -> QuantGreen
                        "Stopped Out" -> QuantRed
                        "Invalidated" -> QuantAmber
                        else -> QuantCyan
                    }
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = QuantTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Expanded full technical report
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                HorizontalDivider(color = QuantBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "TECHNICAL REPORT DETAILS",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = QuantCyan
                )
                Spacer(modifier = Modifier.height(6.dp))

                PriceLevelRow(
                    label = "Safe Entry Min/Max",
                    price = (signal.safeEntryMin + signal.safeEntryMax) / 2.0,
                    subtext = String.format(Locale.US, "Range: $%,.2f – $%,.2f", signal.safeEntryMin, signal.safeEntryMax),
                    accentColor = QuantCyan
                )
                PriceLevelRow(
                    label = "Take-Profit 1 (Conservative)",
                    price = signal.takeProfit1,
                    accentColor = QuantLime
                )
                PriceLevelRow(
                    label = "Take-Profit 2 (Extended)",
                    price = signal.takeProfit2,
                    accentColor = QuantGreen
                )
                PriceLevelRow(
                    label = "Stop-Loss (Risk Control)",
                    price = signal.stopLoss,
                    accentColor = QuantRed
                )
                PriceLevelRow(
                    label = "Invalidation Level",
                    price = signal.invalidationLevel,
                    accentColor = QuantAmber
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Position Sizing Breakdown:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuantTextPrimary
                )
                Text(
                    text = String.format(
                        Locale.US,
                        "Max Risk: $%,.2f (%.1f%%) • Units: %,.4f • Notional Value: $%,.2f",
                        signal.maxDollarRisk,
                        signal.riskPercentage,
                        signal.positionSizeUnits,
                        signal.totalPositionValue
                    ),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = QuantTextSecondary
                )
            }
        }
    }

    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Update Trade Status") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Active", "Hit TP1", "Hit TP2", "Stopped Out", "Invalidated").forEach { st ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (signal.status == st) QuantDarkSurfaceElevated else Color.Transparent)
                                .clickable {
                                    onStatusChange(st)
                                    showStatusDialog = false
                                }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = st,
                                fontWeight = if (signal.status == st) FontWeight.Bold else FontWeight.Normal,
                                color = if (signal.status == st) QuantCyan else QuantTextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
