package com.hatcorp.icalc.converter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow

enum class ActiveInput { FROM, TO }
enum class ExpandedMenu { FROM, TO }

data class UnitConverterState(
    val category: ConversionCategory,
    val fromValue: String = "1",
    val toValue: String = "",
    val fromUnit: UnitInfo,
    val toUnit: UnitInfo,
    val activeInput: ActiveInput = ActiveInput.FROM, // Tracks which value the keypad modifies
    val expandedMenu: ExpandedMenu? = null
)

sealed interface ConverterAction {
    data class KeyPress(val key: String) : ConverterAction
    data class FromUnitChanged(val unit: UnitInfo) : ConverterAction
    data class ToUnitChanged(val unit: UnitInfo) : ConverterAction
    data class SetActiveInput(val input: ActiveInput) : ConverterAction
    data class ToggleMenu(val menu: ExpandedMenu) : ConverterAction
    data object CloseMenus : ConverterAction
}

class UnitConverterViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val categoryName: String = savedStateHandle["category"] ?: "Length"
    private val category = ConversionCategory.fromString(categoryName) ?: ConversionCategory.Length

    private val _state = MutableStateFlow(
        UnitConverterState(
            category = category,
            fromUnit = category.units.first(),
            toUnit = category.units.getOrNull(1) ?: category.units.first()
        )
    )
    val state = _state.asStateFlow()

    init {
        convert()
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

    private fun handleKeyPress(key: String) {
        if (_state.value.activeInput != ActiveInput.FROM) return // Only FROM is editable

        var currentVal = _state.value.fromValue
        when (key) {
            "C" -> currentVal = "0"
            "DEL" -> currentVal = if (currentVal.length > 1) currentVal.dropLast(1) else "0"
            "." -> if (!currentVal.contains(".")) currentVal += "."
            else -> { // Digit
                if (currentVal == "0") currentVal = key
                else if (currentVal.length < 15) currentVal += key
            }
        }
        _state.update { it.copy(fromValue = currentVal) }
    }

    private fun convert() {
        val state = _state.value
        val fromValueDouble = state.fromValue.toDoubleOrNull() ?: 0.0
        val fromUnit = state.fromUnit
        val toUnit = state.toUnit

        if (state.category is ConversionCategory.Temperature) {
            val result = when (fromUnit.name) {
                "Celsius" -> when (toUnit.name) {
                    "Fahrenheit" -> (fromValueDouble * 9 / 5) + 32
                    "Kelvin" -> fromValueDouble + 273.15
                    else -> fromValueDouble
                }
                "Fahrenheit" -> when (toUnit.name) {
                    "Celsius" -> (fromValueDouble - 32) * 5 / 9
                    "Kelvin" -> (fromValueDouble - 32) * 5 / 9 + 273.15
                    else -> fromValueDouble
                }
                "Kelvin" -> when (toUnit.name) {
                    "Celsius" -> fromValueDouble - 273.15
                    "Fahrenheit" -> (fromValueDouble - 273.15) * 9 / 5 + 32
                    else -> fromValueDouble
                }
                else -> 0.0
            }
            _state.update { it.copy(toValue = String.format("%.2f", result)) }
            return
        }

        val valueInBase = fromValueDouble * fromUnit.toBaseRate
        val result = if (toUnit.toBaseRate != 0.0) valueInBase / toUnit.toBaseRate else 0.0

        _state.update { it.copy(toValue = result.toString()) }
    }
}