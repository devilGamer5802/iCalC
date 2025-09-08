package com.hatcorp.icalc.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CurrencyConverterState(
    val fromValue: String = "1",
    val toValue: String = "",
    val fromCurrency: String = "EUR",
    val toCurrency: String = "USD",
    val currencies: List<String> = emptyList(),
    val rates: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class CurrencyViewModel : ViewModel() {
    private val _state = MutableStateFlow(CurrencyConverterState())
    val state = _state.asStateFlow()

    init {
        fetchRates()
    }

    private fun fetchRates() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val response = CurrencyApi.retrofitService.getLatestRates()
                // The API returns rates relative to EUR. Let's add EUR to the map.
                val ratesWithBase = response.rates + ("EUR" to 1.0)
                _state.update {
                    it.copy(
                        isLoading = false,
                        rates = ratesWithBase,
                        currencies = ratesWithBase.keys.sorted()
                    )
                }
                convert()
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Failed to fetch rates: ${e.message}") }
            }
        }
    }

    fun onAction(action: CurrencyAction) {
        when(action) {
            is CurrencyAction.AmountChange -> _state.update { it.copy(fromValue = action.amount) }
            is CurrencyAction.FromCurrencyChange -> _state.update { it.copy(fromCurrency = action.currency) }
            is CurrencyAction.ToCurrencyChange -> _state.update { it.copy(toCurrency = action.currency) }
            CurrencyAction.SwapCurrencies -> _state.update { it.copy(
                fromCurrency = it.toCurrency,
                toCurrency = it.fromCurrency,
                fromValue = it.toValue
            ) }
        }
        convert()
    }

    private fun convert() {
        val amount = _state.value.fromValue.toDoubleOrNull() ?: 0.0
        val fromRate = _state.value.rates[_state.value.fromCurrency] ?: 0.0
        val toRate = _state.value.rates[_state.value.toCurrency] ?: 0.0

        if (toRate != 0.0) {
            // Convert amount to EUR first, then to the target currency
            val amountInEur = amount / fromRate
            val result = amountInEur * toRate
            _state.update { it.copy(toValue = String.format("%.2f", result)) }
        } else {
            _state.update { it.copy(toValue = "") }
        }
    }
}

// Sealed interface for actions
sealed interface CurrencyAction {
    data class AmountChange(val amount: String): CurrencyAction
    data class FromCurrencyChange(val currency: String): CurrencyAction
    data class ToCurrencyChange(val currency: String): CurrencyAction
    data object SwapCurrencies: CurrencyAction
}