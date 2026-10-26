package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.SignalConfidence
import com.example.data.model.SignalType
import com.example.data.model.TradeSignal

@Entity(tableName = "trade_signals")
data class TradeSignalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetPair: String,
    val timeframe: String,
    val recommendation: String, // BUY, SELL, HOLD
    val confidence: String, // LOW, MEDIUM, HIGH
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
    val keyRisksJson: String,
    val fullReport: String,
    val status: String,
    val timestamp: Long
) {
    fun toDomainModel(): TradeSignal {
        val risksList = try {
            if (keyRisksJson.isNotBlank()) {
                val array = org.json.JSONArray(keyRisksJson)
                (0 until array.length()).map { array.getString(it) }
            } else emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        return TradeSignal(
            id = id,
            assetPair = assetPair,
            timeframe = timeframe,
            recommendation = try { SignalType.valueOf(recommendation) } catch (_: Exception) { SignalType.HOLD },
            confidence = try { SignalConfidence.valueOf(confidence) } catch (_: Exception) { SignalConfidence.MEDIUM },
            setupType = setupType,
            macroTrend = macroTrend,
            microMomentum = microMomentum,
            movingAveragesSummary = movingAveragesSummary,
            rsiDivergenceSummary = rsiDivergenceSummary,
            volumeProfileSummary = volumeProfileSummary,
            safeEntryMin = safeEntryMin,
            safeEntryMax = safeEntryMax,
            takeProfit1 = takeProfit1,
            takeProfit2 = takeProfit2,
            stopLoss = stopLoss,
            rrr = rrr,
            accountBalance = accountBalance,
            riskPercentage = riskPercentage,
            maxDollarRisk = maxDollarRisk,
            stopLossDistancePercent = stopLossDistancePercent,
            positionSizeUnits = positionSizeUnits,
            totalPositionValue = totalPositionValue,
            invalidationLevel = invalidationLevel,
            keyRisks = risksList,
            fullReport = fullReport,
            status = status,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromDomain(signal: TradeSignal): TradeSignalEntity {
            val jsonArray = org.json.JSONArray()
            signal.keyRisks.forEach { jsonArray.put(it) }

            return TradeSignalEntity(
                id = signal.id,
                assetPair = signal.assetPair,
                timeframe = signal.timeframe,
                recommendation = signal.recommendation.name,
                confidence = signal.confidence.name,
                setupType = signal.setupType,
                macroTrend = signal.macroTrend,
                microMomentum = signal.microMomentum,
                movingAveragesSummary = signal.movingAveragesSummary,
                rsiDivergenceSummary = signal.rsiDivergenceSummary,
                volumeProfileSummary = signal.volumeProfileSummary,
                safeEntryMin = signal.safeEntryMin,
                safeEntryMax = signal.safeEntryMax,
                takeProfit1 = signal.takeProfit1,
                takeProfit2 = signal.takeProfit2,
                stopLoss = signal.stopLoss,
                rrr = signal.rrr,
                accountBalance = signal.accountBalance,
                riskPercentage = signal.riskPercentage,
                maxDollarRisk = signal.maxDollarRisk,
                stopLossDistancePercent = signal.stopLossDistancePercent,
                positionSizeUnits = signal.positionSizeUnits,
                totalPositionValue = signal.totalPositionValue,
                invalidationLevel = signal.invalidationLevel,
                keyRisksJson = jsonArray.toString(),
                fullReport = signal.fullReport,
                status = signal.status,
                timestamp = signal.timestamp
            )
        }
    }
}
