package com.hatcorp.icalc.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatcorp.icalc.converter.* // Import shared actions and enums
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// State for the currency screen, mirroring the UnitConverterState structure for UI compatibility.
data class CurrencyState(
    val fromValue: String = "1",
    val toValue: String = "",
    val fromUnit: UnitInfo = UnitInfo("Euro", "EUR", 1.0), // Default value before API load
    val toUnit: UnitInfo = UnitInfo("US Dollar", "USD", 0.0), // Default, will be updated
    val availableUnits: List<UnitInfo> = emptyList(),
    val rates: Map<String, Double> = emptyMap(),
    val activeInput: ActiveInput = ActiveInput.FROM,
    val expandedMenu: ExpandedMenu? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class CurrencyViewModel : ViewModel() {
    private val _state = MutableStateFlow(CurrencyState())
    val state = _state.asStateFlow()

    init {
        fetchRates()
    }

    fun onAction(action: ConverterAction) {
        when (action) {
            is ConverterAction.KeyPress -> handleKeyPress(action.key)
            is ConverterAction.FromUnitChanged -> _state.update { it.copy(fromUnit = action.unit) }
            is ConverterAction.ToUnitChanged -> _state.update { it.copy(toUnit = action.unit) }
            is ConverterAction.SetActiveInput -> _state.update { it.copy(activeInput = action.input) }
            is ConverterAction.ToggleMenu -> _state.update {
                it.copy(expandedMenu = if (it.expandedMenu == action.menu) null else action.menu)
            }

            is ConverterAction.CloseMenus -> _state.update { it.copy(expandedMenu = null) }
        }
        convert()
    }

    private fun fetchRates() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = CurrencyApi.retrofitService.getLatestRates()
                val ratesWithBase = response.rates + ("EUR" to 1.0)

                // ADAPTER PATTERN: Convert API data (Map<String, Double>) to the format our UI needs (List<UnitInfo>)
                val units = ratesWithBase.keys.sorted().map { currencyCode ->
                    UnitInfo(
                        name = mapCodeToName(currencyCode), // Use a helper for full names
                        symbol = currencyCode,
                        toBaseRate = 0.0 // Not used for currency conversion, just for data class compatibility
                    )
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        rates = ratesWithBase,
                        availableUnits = units,
                        // Update from/to units with loaded data to ensure they are valid
                        fromUnit = units.find { unit -> unit.symbol == "EUR" } ?: units.first(),
                        toUnit = units.find { unit -> unit.symbol == "USD" }
                            ?: units.getOrElse(1) { units.first() }
                    )
                }
                convert() // Perform initial conversion
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to fetch exchange rates. Please check your internet connection."
                    )
                }
            }
        }
    }

    private fun handleKeyPress(key: String) {
        if (_state.value.activeInput != ActiveInput.FROM) return

        var current = _state.value.fromValue
        when (key) {
            "C" -> current = "0"
            "DEL" -> current = if (current.length > 1) current.dropLast(1) else "0"
            "." -> if (!current.contains(".")) current += "."
            else -> { // Digit
                if (current == "0" || current == "0.0") current = key
                else if (current.length < 15) current += key
            }
        }
        _state.update { it.copy(fromValue = current) }
    }

    private fun convert() {
        val state = _state.value
        if (state.rates.isEmpty()) return // Don't convert if rates aren't loaded

        val amount = state.fromValue.toDoubleOrNull() ?: 0.0
        val fromRate = state.rates[state.fromUnit.symbol] ?: 0.0
        val toRate = state.rates[state.toUnit.symbol] ?: 0.0

        if (fromRate != 0.0) {
            val amountInEur = amount / fromRate // Convert input amount to the base currency (EUR)
            val result = amountInEur * toRate // Convert from base currency to the target currency
            _state.update { it.copy(toValue = String.format("%.2f", result)) }
        } else {
            _state.update { it.copy(toValue = "") }
        }
    }

    // Helper function to provide full names for common currencies, making the UI better.
    private fun mapCodeToName(code: String): String {
        return when (code) {
            "USD" -> "US Dollar"
            "EUR" -> "Euro"
            "JPY" -> "Japanese Yen"
            "GBP" -> "British Pound"
            "AUD" -> "Australian Dollar"
            "CAD" -> "Canadian Dollar"
            "CHF" -> "Swiss Franc"
            "CNY" -> "Chinese Yuan"
            "INR" -> "Indian Rupee"
            // You can add more currency codes and their full names here
            else -> code
        }
    }
}