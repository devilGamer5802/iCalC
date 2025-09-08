package com.hatcorp.icalc.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.*

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state = _state.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Scientific -> performScientificOperation(action.operation)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Clear -> _state.value = CalculatorState()
            is CalculatorAction.Delete -> performDeletion() // Placeholder for AC/Delete logic
            is CalculatorAction.ToggleMode -> {
                _state.update {
                    it.copy(
                        mode = if (it.mode == CalculatorMode.Basic) CalculatorMode.Scientific else CalculatorMode.Basic
                    )
                }
            }
        }
    }

    private fun enterNumber(number: Int) {
        _state.update {
            if (it.operation == null) {
                // Entering number1
                if (it.number1.length >= MAX_NUM_LENGTH) return
                it.copy(number1 = it.number1 + number)
            } else {
                // Entering number2
                if (it.number2.length >= MAX_NUM_LENGTH) return
                it.copy(number2 = it.number2 + number)
            }
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        if (_state.value.number1.isNotBlank()) {
            _state.update { it.copy(operation = operation) }
        }
    }

    private fun enterDecimal() {
        _state.update {
            if (it.operation == null) {
                if (it.number1.contains(".") || it.number1.isBlank()) return@update it
                it.copy(number1 = it.number1 + ".")
            } else {
                if (it.number2.contains(".") || it.number2.isBlank()) return@update it
                it.copy(number2 = it.number2 + ".")
            }
        }
    }

    private fun performScientificOperation(operation: ScientificOperation) {
        if (_state.value.number1.isBlank()) {
            if (operation is ScientificOperation.Pi) {
                _state.update { it.copy(number1 = Math.PI.toString().take(15)) }
            }
            return
        }

        val targetNumber = _state.value.number1.toDoubleOrNull() ?: return

        val result = when (operation) {
            ScientificOperation.Sin -> sin(Math.toRadians(targetNumber))
            ScientificOperation.Cos -> cos(Math.toRadians(targetNumber))
            ScientificOperation.Tan -> tan(Math.toRadians(targetNumber))
            ScientificOperation.Log -> log10(targetNumber)
            ScientificOperation.Ln -> ln(targetNumber)
            ScientificOperation.Sqrt -> sqrt(targetNumber)
            ScientificOperation.Square -> targetNumber.pow(2)
            ScientificOperation.Pi -> return // Already handled
        }

        _state.update {
            it.copy(number1 = result.toString().take(15))
        }
    }

    private fun performCalculation() {
        val number1 = _state.value.number1.toDoubleOrNull()
        val number2 = _state.value.number2.toDoubleOrNull()

        if(number1 != null && number2 != null && _state.value.operation == CalculatorOperation.Percent){
            val result = number1 * (number2 / 100.0)
            _state.update {
                it.copy(
                    number1 = result.toString().take(15),
                    number2 = "",
                    operation = null
                )
            }
            return
        }


        if (number1 == null || number2 == null) {
            return
        }

        val result = when (_state.value.operation) {
            is CalculatorOperation.Add -> number1 + number2
            is CalculatorOperation.Subtract -> number1 - number2
            is CalculatorOperation.Multiply -> number1 * number2
            is CalculatorOperation.Divide -> if(number2 != 0.0) number1 / number2 else Double.NaN
            is CalculatorOperation.Percent -> return // Already handled above
            null -> return
        }

        val resultString = if (result.isNaN()) "Error" else result.toString().take(15)

        _state.update {
            it.copy(
                number1 = resultString,
                number2 = "",
                operation = null
            )
        }
    }

    private fun performDeletion() {
        _state.update {
            if (it.number2.isNotBlank()) {
                it.copy(number2 = it.number2.dropLast(1))
            } else if (it.operation != null) {
                it.copy(operation = null)
            } else if (it.number1.isNotBlank()) {
                it.copy(number1 = it.number1.dropLast(1))
            } else {
                it // No change
            }
        }
    }

    companion object {
        private const val MAX_NUM_LENGTH = 8
    }
}