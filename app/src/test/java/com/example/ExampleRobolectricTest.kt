package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PositionRiskResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CryptoQuant", appName)
    }

    @Test
    fun `position sizing calculation exact formulas`() {
        // Balance: $10,000, Risk: 1% ($100), Entry: 66,000, SL: 64,800 (Diff: 1,200), TP1: 68,400 (Diff: 2,400)
        val result = PositionRiskResult.calculate(
            accountBalance = 10000.0,
            riskPercentage = 1.0,
            entryPrice = 66000.0,
            stopLossPrice = 64800.0,
            takeProfit1 = 68400.0,
            takeProfit2 = 70800.0,
            leverage = 1
        )

        // 1. Max Dollar Risk = 10,000 * 0.01 = 100.0
        assertEquals(100.0, result.maxDollarRisk, 0.001)

        // 2. SL Distance % = |66,000 - 64,800| / 66,000 * 100 = 1.818%
        assertEquals(1.81818, result.stopLossDistancePercent, 0.01)

        // 3. Position Size = 100 / 1,200 = 0.08333 units
        assertEquals(0.08333, result.positionSizeUnits, 0.001)

        // 4. Total Position Value = 0.08333 * 66,000 = 5,500.0
        assertEquals(5500.0, result.totalPositionValue, 0.1)

        // 5. RRR Target 1 = 2,400 / 1,200 = 2.0 (>= 1:2)
        assertEquals(2.0, result.rrrTarget1, 0.001)
        assertTrue(result.isRrrValid)
    }
}
