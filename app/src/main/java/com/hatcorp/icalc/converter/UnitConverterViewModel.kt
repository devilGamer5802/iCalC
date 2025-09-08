package com.hatcorp.icalc.converter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UnitConverterViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryName: String = savedStateHandle["category"] ?: "Length"
    private val category = ConversionCategory.fromString(categoryName) ?: ConversionCategory.Length

    private val _state = MutableStateFlow(UnitConverterState(
        category = category,
        fromUnit = category.units.first(),
        toUnit = category.units.getOrNull(1) ?: category.units.first()
    ))
    val state = _state.asStateFlow()

    fun onAction(action: ConverterAction) {
        when (action) {
            is ConverterAction.FromValueChanged -> {
                _state.update { it.copy(fromValue = action.value) }
                convert()
            }
            is ConverterAction.FromUnitChanged -> {
                _state.update { it.copy(fromUnit = action.unit) }
                convert()
            }
            is ConverterAction.ToUnitChanged -> {
                _state.update { it.copy(toUnit = action.unit) }
                convert()
            }
            is ConverterAction.SwapUnits -> {
                _state.update {
                    it.copy(
                        fromUnit = it.toUnit,
                        toUnit = it.fromUnit,
                        fromValue = it.toValue
                    )
                }
                convert()
            }
        }
    }

    private fun convert() {
        val fromValueDouble = _state.value.fromValue.toDoubleOrNull() ?: 0.0
        val fromUnit = _state.value.fromUnit
        val toUnit = _state.value.toUnit

        val valueInBase = fromValueDouble * fromUnit.toBaseRate
        val result = valueInBase / toUnit.toBaseRate

        _state.update { it.copy(toValue = result.toString()) }
    }
}

// Helper State and Action classes
data class UnitConverterState(
    val category: ConversionCategory,
    val fromValue: String = "1",
    val toValue: String = "",
    val fromUnit: UnitInfo,
    val toUnit: UnitInfo,
)

sealed interface ConverterAction {
    data class FromValueChanged(val value: String) : ConverterAction
    data class FromUnitChanged(val unit: UnitInfo) : ConverterAction
    data class ToUnitChanged(val unit: UnitInfo) : ConverterAction
    data object SwapUnits : ConverterAction
}