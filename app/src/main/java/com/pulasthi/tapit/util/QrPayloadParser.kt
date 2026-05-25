package com.pulasthi.tapit.util

import com.pulasthi.tapit.viewmodel.QrPaymentDetails
import org.json.JSONObject
import java.net.URLDecoder

object QrPayloadParser {

    fun parse(raw: String): QrPaymentDetails {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) {
            return QrPaymentDetails(merchantName = "Unknown Merchant")
        }

        parseJson(trimmed)?.let { return it }
        parseQueryString(trimmed)?.let { return it }

        return QrPaymentDetails(
            merchantName = trimmed,
            accountReference = trimmed,
        )
    }

    private fun parseJson(raw: String): QrPaymentDetails? {
        if (!raw.startsWith("{")) return null
        return try {
            val json = JSONObject(raw)
            QrPaymentDetails(
                merchantName = json.optString("merchant")
                    .ifBlank { json.optString("merchantName") }
                    .ifBlank { json.optString("name") }
                    .ifBlank { "Merchant" },
                amount = json.optString("amount").toAmountOrNull(),
                accountReference = json.optString("account")
                    .ifBlank { json.optString("accountNumber") },
                bankName = json.optString("bank").ifBlank { json.optString("bankName") },
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun parseQueryString(raw: String): QrPaymentDetails? {
        val query = when {
            raw.contains("?") -> raw.substringAfter("?")
            raw.contains("&") || raw.contains("=") -> raw
            else -> return null
        }

        val params = query.split("&")
            .mapNotNull { part ->
                val pieces = part.split("=", limit = 2)
                if (pieces.size == 2) {
                    decode(pieces[0].lowercase()) to decode(pieces[1])
                } else {
                    null
                }
            }
            .toMap()

        if (params.isEmpty()) return null

        return QrPaymentDetails(
            merchantName = params["merchant"]
                ?: params["merchantname"]
                ?: params["name"]
                ?: "Merchant",
            amount = params["amount"]?.toAmountOrNull(),
            accountReference = params["account"] ?: params["accountnumber"],
            bankName = params["bank"] ?: params["bankname"],
        )
    }

    private fun decode(value: String): String =
        runCatching { URLDecoder.decode(value, Charsets.UTF_8.name()) }.getOrDefault(value)

    private fun String.toAmountOrNull(): Double? =
        replace(",", "")
            .toDoubleOrNull()
            ?.takeIf { it > 0.0 }
}
