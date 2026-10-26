package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiChartAnalyzer
import com.example.data.local.AppDatabase
import com.example.data.model.PositionRiskResult
import com.example.data.model.TradeSignal
import com.example.data.repository.TradeSignalRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AnalysisUiState {
    data object Idle : AnalysisUiState
    data class Analyzing(val step: String) : AnalysisUiState
    data class Success(val signal: TradeSignal) : AnalysisUiState
    data class Error(val message: String) : AnalysisUiState
}

class QuantViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TradeSignalRepository

    init {
        val database = AppDatabase.getInstance(application)
        val analyzer = GeminiChartAnalyzer(application)
        repository = TradeSignalRepository(database.tradeSignalDao(), analyzer)
    }

    val savedSignals: StateFlow<List<TradeSignal>> = repository.allSignals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Analysis Screen State
    private val _analysisState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Idle)
    val analysisState: StateFlow<AnalysisUiState> = _analysisState.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _selectedPreset = MutableStateFlow<String?>("btc_4h")
    val selectedPreset: StateFlow<String?> = _selectedPreset.asStateFlow()

    private val _assetPair = MutableStateFlow("BTC/USDT")
    val assetPair: StateFlow<String> = _assetPair.asStateFlow()

    private val _timeframe = MutableStateFlow("4h")
    val timeframe: StateFlow<String> = _timeframe.asStateFlow()

    private val _accountBalance = MutableStateFlow(10000.0)
    val accountBalance: StateFlow<Double> = _accountBalance.asStateFlow()

    private val _riskPercentage = MutableStateFlow(1.0)
    val riskPercentage: StateFlow<Double> = _riskPercentage.asStateFlow()

    private val _userApiKey = MutableStateFlow("")
    val userApiKey: StateFlow<String> = _userApiKey.asStateFlow()

    // Notification events
    private val _eventMessage = MutableSharedFlow<String>()
    val eventMessage: SharedFlow<String> = _eventMessage.asSharedFlow()

    // Standalone Calculator State
    private val _calcBalance = MutableStateFlow(10000.0)
    val calcBalance: StateFlow<Double> = _calcBalance.asStateFlow()

    private val _calcRiskPct = MutableStateFlow(1.0)
    val calcRiskPct: StateFlow<Double> = _calcRiskPct.asStateFlow()

    private val _calcEntry = MutableStateFlow(66000.0)
    val calcEntry: StateFlow<Double> = _calcEntry.asStateFlow()

    private val _calcSl = MutableStateFlow(64800.0)
    val calcSl: StateFlow<Double> = _calcSl.asStateFlow()

    private val _calcTp1 = MutableStateFlow(68400.0)
    val calcTp1: StateFlow<Double> = _calcTp1.asStateFlow()

    private val _calcTp2 = MutableStateFlow(70800.0)
    val calcTp2: StateFlow<Double> = _calcTp2.asStateFlow()

    private val _calcLeverage = MutableStateFlow(1)
    val calcLeverage: StateFlow<Int> = _calcLeverage.asStateFlow()

    private val _calcResult = MutableStateFlow(
        PositionRiskResult.calculate(10000.0, 1.0, 66000.0, 64800.0, 68400.0, 70800.0, 1)
    )
    val calcResult: StateFlow<PositionRiskResult> = _calcResult.asStateFlow()

    // Matrix Alignment State
    private val _matrix12h = MutableStateFlow("Above 200 EMA (Bullish)")
    val matrix12h: StateFlow<String> = _matrix12h.asStateFlow()

    private val _matrix4h = MutableStateFlow("Bullish EMA 20/50 Alignment")
    val matrix4h: StateFlow<String> = _matrix4h.asStateFlow()

    private val _matrix1h = MutableStateFlow("Bullish Volume Bounce (No Div)")
    val matrix1h: StateFlow<String> = _matrix1h.asStateFlow()

    fun setImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
        if (uri != null) {
            _selectedPreset.value = null
        }
    }

    fun selectPreset(preset: String) {
        _selectedPreset.value = preset
        _selectedImageUri.value = null
        when (preset) {
            "btc_4h" -> {
                _assetPair.value = "BTC/USDT"
                _timeframe.value = "4h"
            }
            "eth_1h" -> {
                _assetPair.value = "ETH/USDT"
                _timeframe.value = "1h"
            }
            "sol_12h" -> {
                _assetPair.value = "SOL/USDT"
                _timeframe.value = "12h"
            }
        }
    }

    fun setAssetPair(pair: String) {
        _assetPair.value = pair
    }

    fun setTimeframe(tf: String) {
        _timeframe.value = tf
    }

    fun setAccountBalance(balance: Double) {
        _accountBalance.value = balance
    }

    fun setRiskPercentage(pct: Double) {
        _riskPercentage.value = pct
    }

    fun setUserApiKey(key: String) {
        _userApiKey.value = key
    }

    fun runChartAnalysis() {
        viewModelScope.launch {
            _analysisState.value = AnalysisUiState.Analyzing("Scanning 20 / 50 / 200 EMA alignments...")
            try {
                delay(400)
                _analysisState.value = AnalysisUiState.Analyzing("Calculating RSI oscillator & divergence patterns...")
                delay(400)
                _analysisState.value = AnalysisUiState.Analyzing("Mapping High & Low Volume Nodes (HVN / LVN)...")
                delay(400)
                _analysisState.value = AnalysisUiState.Analyzing("Computing exact position sizing & safe price model...")

                val result = repository.analyzeChart(
                    imageUri = _selectedImageUri.value,
                    presetId = _selectedPreset.value,
                    assetPair = _assetPair.value,
                    timeframe = _timeframe.value,
                    accountBalance = _accountBalance.value,
                    riskPercentage = _riskPercentage.value,
                    customApiKey = _userApiKey.value.ifBlank { null }
                )

                _analysisState.value = AnalysisUiState.Success(result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _analysisState.value = AnalysisUiState.Error(e.localizedMessage ?: "Analysis encountered an unexpected issue")
            }
        }
    }

    fun saveCurrentSignal(signal: TradeSignal) {
        viewModelScope.launch {
            try {
                repository.saveSignal(signal)
                _eventMessage.emit("Signal saved to Trade Journal!")
            } catch (e: Exception) {
                _eventMessage.emit("Failed to save signal: ${e.localizedMessage}")
            }
        }
    }

    fun updateSignalStatus(id: Long, newStatus: String) {
        viewModelScope.launch {
            try {
                repository.updateSignalStatus(id, newStatus)
                _eventMessage.emit("Status updated to $newStatus")
            } catch (e: Exception) {
                _eventMessage.emit("Failed to update status")
            }
        }
    }

    fun deleteSignal(signal: TradeSignal) {
        viewModelScope.launch {
            try {
                repository.deleteSignal(signal)
                _eventMessage.emit("Signal deleted from journal")
            } catch (e: Exception) {
                _eventMessage.emit("Failed to delete signal")
            }
        }
    }

    // Calculator updates
    fun updateCalcParams(
        balance: Double = _calcBalance.value,
        riskPct: Double = _calcRiskPct.value,
        entry: Double = _calcEntry.value,
        sl: Double = _calcSl.value,
        tp1: Double = _calcTp1.value,
        tp2: Double = _calcTp2.value,
        leverage: Int = _calcLeverage.value
    ) {
        _calcBalance.value = balance
        _calcRiskPct.value = riskPct
        _calcEntry.value = entry
        _calcSl.value = sl
        _calcTp1.value = tp1
        _calcTp2.value = tp2
        _calcLeverage.value = leverage

        _calcResult.value = PositionRiskResult.calculate(
            accountBalance = balance,
            riskPercentage = riskPct,
            entryPrice = entry,
            stopLossPrice = sl,
            takeProfit1 = tp1,
            takeProfit2 = tp2,
            leverage = leverage
        )
    }

    // Matrix updates
    fun setMatrix12h(value: String) {
        _matrix12h.value = value
    }

    fun setMatrix4h(value: String) {
        _matrix4h.value = value
    }

    fun setMatrix1h(value: String) {
        _matrix1h.value = value
    }
}
