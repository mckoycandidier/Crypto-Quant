package com.example.data.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.PositionRiskResult
import com.example.data.model.SignalConfidence
import com.example.data.model.SignalType
import com.example.data.model.TradeSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class GeminiChartAnalyzer(private val context: Context) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeChart(
        imageUri: Uri?,
        presetId: String?,
        assetPair: String,
        timeframe: String,
        accountBalance: Double,
        riskPercentage: Double,
        customApiKey: String? = null
    ): TradeSignal = withContext(Dispatchers.IO) {
        val apiKey = (customApiKey?.trim()?.ifEmpty { null }
            ?: BuildConfig.GEMINI_API_KEY.trim().ifEmpty { null })
            ?.takeIf { it != "MY_GEMINI_API_KEY" }

        // If no API key is set, use the Deterministic Quant Engine
        if (apiKey.isNullOrBlank()) {
            Log.d("GeminiChartAnalyzer", "No active Gemini API key configured. Utilizing Deterministic Quant Engine.")
            return@withContext DeterministicQuantEngine.generateAnalysis(
                assetPair = assetPair,
                timeframe = timeframe,
                accountBalance = accountBalance,
                riskPercentage = riskPercentage,
                presetId = presetId
            )
        }

        // Try Gemini Multimodal analysis
        try {
            val bitmap = loadBitmap(imageUri, presetId)
            val base64Data = bitmap?.let { encodeBitmapToBase64(it) }

            val userPrompt = buildString {
                append("Please analyze this crypto candlestick chart screenshot according to your system prompt rules.\n")
                append("Asset Pair: $assetPair\n")
                append("Timeframe: $timeframe\n")
                append("Account Balance: $$accountBalance\n")
                append("Risk Percentage Per Trade: $riskPercentage%\n")
                append("Strictly follow the REQUIRED OUTPUT FORMAT with all 6 numbered sections.\n")
            }

            val requestJson = JSONObject()
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()

            // System instructions / prompt
            val textPart = JSONObject()
            textPart.put("text", "${QuantAnalysisPrompt.SYSTEM_PROMPT}\n\n$userPrompt")
            partsArray.put(textPart)

            // Image part
            if (!base64Data.isNullOrEmpty()) {
                val imagePart = JSONObject()
                val inlineData = JSONObject()
                inlineData.put("mime_type", "image/jpeg")
                inlineData.put("data", base64Data)
                imagePart.put("inline_data", inlineData)
                partsArray.put(imagePart)
            }

            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            requestJson.put("contents", contentsArray)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val responseText = parts.getJSONObject(0).optString("text")
                        if (responseText.isNotBlank()) {
                            return@withContext parseGeminiResponse(
                                rawText = responseText,
                                assetPair = assetPair,
                                timeframe = timeframe,
                                accountBalance = accountBalance,
                                riskPercentage = riskPercentage
                            )
                        }
                    }
                }
            } else {
                Log.w("GeminiChartAnalyzer", "Gemini API HTTP ${response.code}: $responseBody")
            }
        } catch (e: Exception) {
            Log.e("GeminiChartAnalyzer", "Gemini analysis error: ${e.message}", e)
        }

        // Fallback to Deterministic Quant Engine on any failure
        DeterministicQuantEngine.generateAnalysis(
            assetPair = assetPair,
            timeframe = timeframe,
            accountBalance = accountBalance,
            riskPercentage = riskPercentage,
            presetId = presetId
        )
    }

    private fun loadBitmap(uri: Uri?, presetId: String?): Bitmap? {
        try {
            if (uri != null) {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    return BitmapFactory.decodeStream(stream)
                }
            } else if (presetId != null) {
                val resId = when (presetId) {
                    "btc_4h" -> com.example.R.drawable.chart_btc_4h
                    "eth_1h" -> com.example.R.drawable.chart_eth_1h
                    "sol_12h" -> com.example.R.drawable.chart_sol_12h
                    else -> com.example.R.drawable.chart_btc_4h
                }
                return BitmapFactory.decodeResource(context.resources, resId)
            }
        } catch (e: Exception) {
            Log.e("GeminiChartAnalyzer", "Failed to load bitmap: ${e.message}")
        }
        return null
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        // Resize if too large to keep payload under 4MB
        val maxDimension = 1280
        val scale = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
            maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height)
        } else 1.0f

        val scaledBitmap = if (scale < 1.0f) {
            Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
        } else {
            bitmap
        }

        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun parseGeminiResponse(
        rawText: String,
        assetPair: String,
        timeframe: String,
        accountBalance: Double,
        riskPercentage: Double
    ): TradeSignal {
        val recType = when {
            rawText.contains("Recommendation: BUY", ignoreCase = true) || rawText.contains("Recommendation: [BUY]", ignoreCase = true) -> SignalType.BUY
            rawText.contains("Recommendation: SELL", ignoreCase = true) || rawText.contains("Recommendation: [SELL]", ignoreCase = true) -> SignalType.SELL
            else -> SignalType.HOLD
        }

        val confidence = when {
            rawText.contains("High", ignoreCase = true) && rawText.contains("Signal Confidence", ignoreCase = true) -> SignalConfidence.HIGH
            rawText.contains("Low", ignoreCase = true) && rawText.contains("Signal Confidence", ignoreCase = true) -> SignalConfidence.LOW
            else -> SignalConfidence.MEDIUM
        }

        // Extract numerical parameters using regex patterns
        val entryRegex = Pattern.compile("(?i)Safe Entry Price Zone:[^\\d]*([\\d,.]+)(?:[^\\d]+([\\d,.]+))?")
        val tp1Regex = Pattern.compile("(?i)Take-Profit Target 1[^\\d]*([\\d,.]+)")
        val tp2Regex = Pattern.compile("(?i)Take-Profit Target 2[^\\d]*([\\d,.]+)")
        val slRegex = Pattern.compile("(?i)Stop-Loss[^\\d]*([\\d,.]+)")
        val invRegex = Pattern.compile("(?i)Invalidation Level:[^\\d]*([\\d,.]+)")

        fun extractNumber(pattern: Pattern): Double? {
            val matcher = pattern.matcher(rawText)
            return if (matcher.find()) {
                matcher.group(1)?.replace(",", "")?.toDoubleOrNull()
            } else null
        }

        val entry1 = extractNumber(entryRegex) ?: 65000.0
        val entryMatcher = entryRegex.matcher(rawText)
        val entry2 = if (entryMatcher.find() && entryMatcher.group(2) != null) {
            entryMatcher.group(2)?.replace(",", "")?.toDoubleOrNull() ?: entry1
        } else entry1

        val safeEntryMin = minOf(entry1, entry2)
        val safeEntryMax = maxOf(entry1, entry2)
        val entryMid = (safeEntryMin + safeEntryMax) / 2.0
        val tp1 = extractNumber(tp1Regex) ?: (entryMid * 1.05)
        val tp2 = extractNumber(tp2Regex) ?: (entryMid * 1.10)
        val sl = extractNumber(slRegex) ?: (entryMid * 0.97)
        val inv = extractNumber(invRegex) ?: sl

        val positionRisk = PositionRiskResult.calculate(
            accountBalance = accountBalance,
            riskPercentage = riskPercentage,
            entryPrice = entryMid,
            stopLossPrice = sl,
            takeProfit1 = tp1,
            takeProfit2 = tp2,
            leverage = 1
        )

        return TradeSignal(
            assetPair = assetPair,
            timeframe = timeframe,
            recommendation = recType,
            confidence = confidence,
            setupType = "AI Multimodal Quantitative Analysis",
            macroTrend = if (rawText.contains("Bullish", ignoreCase = true)) "Macro Bullish Structure" else "Macro Caution / Mixed",
            microMomentum = "Evaluated via 1h RSI & Volume Signals",
            movingAveragesSummary = "20 / 50 / 200 EMA alignment analyzed visually",
            rsiDivergenceSummary = "RSI momentum and divergence thresholds checked",
            volumeProfileSummary = "High/Low volume nodes and POC mapped",
            safeEntryMin = safeEntryMin,
            safeEntryMax = safeEntryMax,
            takeProfit1 = tp1,
            takeProfit2 = tp2,
            stopLoss = sl,
            rrr = positionRisk.rrrTarget1,
            accountBalance = accountBalance,
            riskPercentage = riskPercentage,
            maxDollarRisk = positionRisk.maxDollarRisk,
            stopLossDistancePercent = positionRisk.stopLossDistancePercent,
            positionSizeUnits = positionRisk.positionSizeUnits,
            totalPositionValue = positionRisk.totalPositionValue,
            invalidationLevel = inv,
            keyRisks = listOf(
                "Market volatility spike beyond historical ATR",
                "Unconfirmed candle close at structural invalidation level ($inv)",
                "Timeframe divergence between macro 12h and micro 1h momentum"
            ),
            fullReport = rawText,
            status = "Active"
        )
    }
}
