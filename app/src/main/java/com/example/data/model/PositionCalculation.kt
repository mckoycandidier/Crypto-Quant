package com.example.data.model

import kotlin.math.abs

data class PositionRiskResult(
    val accountBalance: Double,
    val riskPercentage: Double,
    val entryPrice: Double,
    val stopLossPrice: Double,
    val takeProfit1: Double,
    val takeProfit2: Double,
    val leverage: Int = 1,
    val maxDollarRisk: Double,
    val stopLossDistancePercent: Double,
    val positionSizeUnits: Double,
    val totalPositionValue: Double,
    val requiredMargin: Double,
    val rrrTarget1: Double,
    val rrrTarget2: Double,
    val isRrrValid: Boolean, // RRR >= 1:2 rule
    val potentialProfitTarget1: Double,
    val potentialProfitTarget2: Double
) {
    companion object {
        fun calculate(
            accountBalance: Double,
            riskPercentage: Double,
            entryPrice: Double,
            stopLossPrice: Double,
            takeProfit1: Double,
            takeProfit2: Double,
            leverage: Int = 1
        ): PositionRiskResult {
            val safeBalance = accountBalance.coerceAtLeast(0.0)
            val safeRiskPct = riskPercentage.coerceIn(0.01, 100.0)
            val safeEntry = entryPrice.coerceAtLeast(0.00000001)
            val safeSl = stopLossPrice.coerceAtLeast(0.0)
            val safeLev = leverage.coerceIn(1, 125)

            // 1. Max Dollar Risk ($) = Account Balance * (Risk Percentage / 100)
            val maxDollarRisk = safeBalance * (safeRiskPct / 100.0)

            // 2. Stop-Loss Distance (%) = (|Entry - SL| / Entry) * 100
            val priceDiff = abs(safeEntry - safeSl)
            val stopLossDistancePercent = if (safeEntry > 0) (priceDiff / safeEntry) * 100.0 else 0.0

            // 3. Position Size (Units) = Max Risk ($) / |Entry - SL|
            val positionSizeUnits = if (priceDiff > 0) maxDollarRisk / priceDiff else 0.0

            // 4. Total Position Value ($ Notional) = Position Size (Units) * Entry Price
            val totalPositionValue = positionSizeUnits * safeEntry

            // Required Margin with Leverage
            val requiredMargin = if (safeLev > 0) totalPositionValue / safeLev else totalPositionValue

            // Risk-to-Reward Ratio (Target minimum 1:2)
            val target1Diff = abs(takeProfit1 - safeEntry)
            val target2Diff = abs(takeProfit2 - safeEntry)
            val rrrTarget1 = if (priceDiff > 0) target1Diff / priceDiff else 0.0
            val rrrTarget2 = if (priceDiff > 0) target2Diff / priceDiff else 0.0

            val potentialProfitTarget1 = positionSizeUnits * target1Diff
            val potentialProfitTarget2 = positionSizeUnits * target2Diff

            val isRrrValid = rrrTarget1 >= 2.0 || rrrTarget2 >= 2.0

            return PositionRiskResult(
                accountBalance = safeBalance,
                riskPercentage = safeRiskPct,
                entryPrice = safeEntry,
                stopLossPrice = safeSl,
                takeProfit1 = takeProfit1,
                takeProfit2 = takeProfit2,
                leverage = safeLev,
                maxDollarRisk = maxDollarRisk,
                stopLossDistancePercent = stopLossDistancePercent,
                positionSizeUnits = positionSizeUnits,
                totalPositionValue = totalPositionValue,
                requiredMargin = requiredMargin,
                rrrTarget1 = rrrTarget1,
                rrrTarget2 = rrrTarget2,
                isRrrValid = isRrrValid,
                potentialProfitTarget1 = potentialProfitTarget1,
                potentialProfitTarget2 = potentialProfitTarget2
            )
        }
    }
}
