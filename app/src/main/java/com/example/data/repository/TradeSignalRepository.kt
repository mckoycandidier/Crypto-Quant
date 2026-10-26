package com.example.data.repository

import android.net.Uri
import com.example.data.ai.GeminiChartAnalyzer
import com.example.data.local.TradeSignalDao
import com.example.data.local.TradeSignalEntity
import com.example.data.model.TradeSignal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TradeSignalRepository(
    private val dao: TradeSignalDao,
    private val analyzer: GeminiChartAnalyzer
) {
    val allSignals: Flow<List<TradeSignal>> = dao.getAllSignals().map { list ->
        list.map { it.toDomainModel() }
    }

    suspend fun saveSignal(signal: TradeSignal): Long {
        return dao.insertSignal(TradeSignalEntity.fromDomain(signal))
    }

    suspend fun updateSignalStatus(id: Long, newStatus: String) {
        dao.updateSignalStatus(id, newStatus)
    }

    suspend fun deleteSignal(signal: TradeSignal) {
        dao.deleteSignal(TradeSignalEntity.fromDomain(signal))
    }

    suspend fun analyzeChart(
        imageUri: Uri?,
        presetId: String?,
        assetPair: String,
        timeframe: String,
        accountBalance: Double,
        riskPercentage: Double,
        customApiKey: String? = null
    ): TradeSignal {
        return analyzer.analyzeChart(
            imageUri = imageUri,
            presetId = presetId,
            assetPair = assetPair,
            timeframe = timeframe,
            accountBalance = accountBalance,
            riskPercentage = riskPercentage,
            customApiKey = customApiKey
        )
    }
}
