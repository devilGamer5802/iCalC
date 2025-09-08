package com.hatcorp.icalc.currency

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)