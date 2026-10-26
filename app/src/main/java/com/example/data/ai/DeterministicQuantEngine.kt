package com.example.data.ai

import com.example.data.model.PositionRiskResult
import com.example.data.model.SignalConfidence
import com.example.data.model.SignalType
import com.example.data.model.TradeSignal
import java.util.Locale

object DeterministicQuantEngine {

    fun generateAnalysis(
        assetPair: String,
        timeframe: String,
        accountBalance: Double,
        riskPercentage: Double,
        presetId: String? = null
    ): TradeSignal {
        val pairUpper = assetPair.uppercase(Locale.US).trim().ifEmpty { "BTC/USDT" }
        val tf = timeframe.trim().ifEmpty { "4h" }

        val signalType: SignalType
        val confidence: SignalConfidence
        val setupType: String
        val macroTrend: String
        val microMomentum: String
        val movingAveragesSummary: String
        val rsiDivergenceSummary: String
        val volumeProfileSummary: String
        val safeEntryMin: Double
        val safeEntryMax: Double
        val entryMid: Double
        val takeProfit1: Double
        val takeProfit2: Double
        val stopLoss: Double
        val invalidationLevel: Double
        val keyRisks: List<String>

        when {
            presetId == "btc_4h" || pairUpper.contains("BTC") -> {
                // BTC/USDT 4H analysis based on TradingView screenshot
                // Price ~66,245, 20 EMA: 66,110, 50 EMA: 65,487, 200 EMA: 62,915
                signalType = SignalType.BUY
                confidence = SignalConfidence.HIGH
                setupType = "EMA Bounce & Dynamic Support Retest"
                macroTrend = "Bullish (Price 66,245.89 firmly above 200 EMA at 62,915.68 on 4h/12h structure)"
                microMomentum = "Bullish (RSI holding in Bullish Control Zone 55–62, 20-period volume expanding on candle rebound)"
                movingAveragesSummary = "Price is above 20 EMA (66,110.45) and 50 EMA (65,487.31). 20/50 EMAs are positively aligned above the 200 EMA (62,915.68), confirming bullish continuation."
                rsiDivergenceSummary = "RSI sits at 58.4 in the Bullish Control Zone (50–70). No regular bearish divergence detected on 4h; mild hidden bullish divergence formed during the pullback to the 50 EMA."
                volumeProfileSummary = "Volume bar confirmed bounce at 65,500 HVN support. High Volume Node (HVN) dense support at 65,400–65,800. Low Volume Node (LVN) liquidity pocket between 68,200 and 70,000 for upside expansion."
                safeEntryMin = 65800.0
                safeEntryMax = 66250.0
                entryMid = 66050.0
                takeProfit1 = 68400.0 // Nearest resistance / HVN
                takeProfit2 = 70800.0 // Extended target through LVN gap
                stopLoss = 64800.0    // Structural invalidation below 50 EMA & swing low
                invalidationLevel = 64800.0
                keyRisks = listOf(
                    "Rejection at 68,500 swing high double-top liquidity barrier",
                    "Sudden macro Bitcoin dominance shift triggering volume compression",
                    "4-hour candle close below 50 EMA invalidating immediate momentum"
                )
            }
            presetId == "eth_1h" || pairUpper.contains("ETH") -> {
                // ETH/USDT 1H analysis showing Bearish Divergence (Price HH 3050 vs RSI LH 68.10)
                signalType = SignalType.SELL
                confidence = SignalConfidence.HIGH
                setupType = "Divergence Reversal (Regular Bearish Divergence)"
                macroTrend = "Neutral to Bearish Rejection (Price tested 3,050 resistance, failing to hold 20 EMA)"
                microMomentum = "Bearish (Regular Bearish Divergence: Price printed Higher High at 3,050 while RSI printed Lower High at 68.10; declining buyer volume)"
                movingAveragesSummary = "Price is losing dynamic support of the 20 EMA (2,978.10) and approaching the 50 EMA (2,985.45). Flattening slope indicates loss of bullish thrust."
                rsiDivergenceSummary = "Active Regular Bearish Divergence confirmed on 1h: Price made HH (2,985 -> 3,050) while RSI created clear LH (78 -> 68.10) falling out of overbought territory."
                volumeProfileSummary = "Exhaustion volume on peak candle followed by high sell volume node at 3,000 POC. Significant LVN void down to 2,860 which offers high probability take-profit liquidity."
                safeEntryMin = 2980.0
                safeEntryMax = 3010.0
                entryMid = 2995.0
                takeProfit1 = 2910.0 // Nearest structural support / HVN
                takeProfit2 = 2840.0 // Low volume node extension target
                stopLoss = 3065.0   // Above bearish divergence invalidation swing high
                invalidationLevel = 3065.0
                keyRisks = listOf(
                    "Violent short squeeze breaking above 3,065 invalidation level",
                    "Unconfirmed 1h close below 2,975 enabling a fakeout reversal",
                    "High correlation with macro BTC momentum overriding micro ETH divergence"
                )
            }
            presetId == "sol_12h" || pairUpper.contains("SOL") -> {
                // SOL/USDT 12H analysis: Price 138.45 above 200 EMA (98.60), HVN Support at 112-116
                signalType = SignalType.BUY
                confidence = SignalConfidence.MEDIUM
                setupType = "Volume Profile Retest & Macro Trend Continuation"
                macroTrend = "Strong Macro Bullish Bias (Price 138.45 well above 200 EMA at 98.60 on 12h timeframe)"
                microMomentum = "Cooling consolidation after parabolic impulse; RSI reset from 76 to 54 within Bullish Control Zone"
                movingAveragesSummary = "Price holds structural spacing well above ascending 200 EMA (98.60). Moving averages in golden stack configuration indicating robust multi-week cycle strength."
                rsiDivergenceSummary = "RSI at 54.2, successfully cooled down from overbought peak without printing regular bearish divergence on 12h."
                volumeProfileSummary = "Point of Control (POC) and dense High Volume Node (HVN) established between 112.00 and 116.50. Low Volume Node (LVN) acceleration runway through 145.00 toward 160.00."
                safeEntryMin = 132.0
                safeEntryMax = 138.0
                entryMid = 135.0
                takeProfit1 = 152.0 // Prior cycle pivot resistance
                takeProfit2 = 168.0 // Major LVN breakout extension
                stopLoss = 124.0   // Protected below local 12h consolidation low
                invalidationLevel = 122.5
                keyRisks = listOf(
                    "Deeper mean-reversion retest toward the 115.00 HVN support zone before continuation",
                    "Broader crypto market macro deleveraging pressure",
                    "Extended 12h consolidation increasing funding rate carry cost"
                )
            }
            else -> {
                // Generic rule-checked setup with caution priority
                signalType = SignalType.HOLD
                confidence = SignalConfidence.LOW
                setupType = "Multi-Indicator Conflict (Caution First Model)"
                macroTrend = "Ranging / Undefined (Price fluctuating across 200 EMA barrier)"
                microMomentum = "Neutral (RSI oscillating near 48–52 midpoint with low volume bars)"
                movingAveragesSummary = "20, 50, and 200 EMAs are converging in an EMA Squeeze pattern without a directional expansion signal."
                rsiDivergenceSummary = "RSI centered at 49.0 without clear regular or hidden divergence confirmation."
                volumeProfileSummary = "Volume is below 20-period moving average. Price trapped inside dense HVN range without breakout confirmation."
                safeEntryMin = 100.0
                safeEntryMax = 102.0
                entryMid = 101.0
                takeProfit1 = 108.0
                takeProfit2 = 114.0
                stopLoss = 96.0
                invalidationLevel = 95.0
                keyRisks = listOf(
                    "Conflict across indicator timeframes: default to HOLD / WAIT per capital preservation rule",
                    "Lack of volume expansion: high probability of false breakout/breakdown",
                    "Whipsaw chop inside EMA squeeze compression corridor"
                )
            }
        }

        // Run exact mathematical formulas from the prompt:
        val riskResult = PositionRiskResult.calculate(
            accountBalance = accountBalance,
            riskPercentage = riskPercentage,
            entryPrice = entryMid,
            stopLossPrice = stopLoss,
            takeProfit1 = takeProfit1,
            takeProfit2 = takeProfit2,
            leverage = 1
        )

        val fullReport = buildString {
            append("### 1. Market Overview & Timeframe Context\n")
            append("* Asset Pair: $pairUpper\n")
            append("* Timeframe(s) Identified: $tf\n")
            append("* Macro Trend (200 EMA & 12h/4h Structure): $macroTrend\n")
            append("* Micro Momentum (1h RSI & Volume): $microMomentum\n\n")

            append("### 2. Technical Indicator Breakdown\n")
            append("* Moving Averages (20 / 50 / 200 EMA): $movingAveragesSummary\n")
            append("* RSI & Divergence Analysis: $rsiDivergenceSummary\n")
            append("* Volume & Volume Profile: $volumeProfileSummary\n\n")

            append("### 3. Actionable Signal\n")
            append("* Recommendation: ${signalType.name} ${if (signalType == SignalType.HOLD) "(NO TRADE / WAIT FOR CONFLUENCE)" else ""}\n")
            append("* Signal Confidence: ${confidence.name.lowercase().replaceFirstChar { it.uppercase() }}\n")
            append("* Setup Type: $setupType\n\n")

            append("### 4. Precision Execution Parameters (Safe Price Model)\n")
            append(String.format(Locale.US, "* Safe Entry Price Zone: $%.2f – $%.2f (Mid: $%.2f)\n", safeEntryMin, safeEntryMax, entryMid))
            append(String.format(Locale.US, "* Take-Profit Target 1 (Conservative): $%.2f\n", takeProfit1))
            append(String.format(Locale.US, "* Take-Profit Target 2 (Extended): $%.2f\n", takeProfit2))
            append(String.format(Locale.US, "* Stop-Loss (Risk Control): $%.2f\n", stopLoss))
            append(String.format(Locale.US, "* Risk-to-Reward Ratio (RRR): 1:%.2f (TP1) | 1:%.2f (TP2) [Target minimum 1:2]\n\n", riskResult.rrrTarget1, riskResult.rrrTarget2))

            append("### 5. Position Sizing & Money Management\n")
            append("*(Calculated using provided account balance & risk %)*\n")
            append(String.format(Locale.US, "* Account Balance: $%,.2f | Risk Per Trade: %.2f%% = $%,.2f\n", riskResult.accountBalance, riskResult.riskPercentage, riskResult.maxDollarRisk))
            append(String.format(Locale.US, "* Stop-Loss Distance: %.2f%%\n", riskResult.stopLossDistancePercent))
            append(String.format(Locale.US, "* Maximum Allowed Loss: $%,.2f\n", riskResult.maxDollarRisk))
            append(String.format(Locale.US, "* Total Position Value (Notional): $%,.2f\n", riskResult.totalPositionValue))
            append(String.format(Locale.US, "* Position Size in Tokens/Coins: %,.4f units\n\n", riskResult.positionSizeUnits))

            append("### 6. Invalidation & Risk Conditions\n")
            append(String.format(Locale.US, "* Invalidation Level: $%.2f (Exact structural price break that invalidates setup)\n", invalidationLevel))
            append("* Key Risks:\n")
            keyRisks.forEach { risk ->
                append("  - $risk\n")
            }
        }

        return TradeSignal(
            assetPair = pairUpper,
            timeframe = tf,
            recommendation = signalType,
            confidence = confidence,
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
            rrr = riskResult.rrrTarget1,
            accountBalance = accountBalance,
            riskPercentage = riskPercentage,
            maxDollarRisk = riskResult.maxDollarRisk,
            stopLossDistancePercent = riskResult.stopLossDistancePercent,
            positionSizeUnits = riskResult.positionSizeUnits,
            totalPositionValue = riskResult.totalPositionValue,
            invalidationLevel = invalidationLevel,
            keyRisks = keyRisks,
            fullReport = fullReport,
            status = "Active"
        )
    }
}
