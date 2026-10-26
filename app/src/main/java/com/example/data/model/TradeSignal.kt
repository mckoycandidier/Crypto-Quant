package com.example.data.model

enum class SignalType {
    BUY,
    SELL,
    HOLD
}

enum class SignalConfidence {
    LOW,
    MEDIUM,
    HIGH
}

data class TradeSignal(
    val id: Long = 0,
    val assetPair: String,
    val timeframe: String,
    val recommendation: SignalType,
    val confidence: SignalConfidence,
    val setupType: String,
    val macroTrend: String,
    val microMomentum: String,
    val movingAveragesSummary: String,
    val rsiDivergenceSummary: String,
    val volumeProfileSummary: String,
    val safeEntryMin: Double,
    val safeEntryMax: Double,
    val takeProfit1: Double,
    val takeProfit2: Double,
    val stopLoss: Double,
    val rrr: Double,
    val accountBalance: Double,
    val riskPercentage: Double,
    val maxDollarRisk: Double,
    val stopLossDistancePercent: Double,
    val positionSizeUnits: Double,
    val totalPositionValue: Double,
    val invalidationLevel: Double,
    val keyRisks: List<String>,
    val fullReport: String,
    val status: String = "Active",
    val timestamp: Long = System.currentTimeMillis()
)
