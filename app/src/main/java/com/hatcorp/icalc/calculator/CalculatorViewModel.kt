package com.hatcorp.icalc.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.*
import com.hatcorp.icalc.data.HistoryRepository
import kotlinx.coroutines.launch

class CalculatorViewModel(private val historyRepository: HistoryRepository) : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state = _state.asStateFlow()
    init {
        // Load history on startup
        viewModelScope.launch {
            historyRepository.historyFlow.collect { historyList ->
                _state.update { it.copy(history = historyList) }
            }
        }
    }
    fun onAction(action: CalculatorAction) {
        when (action) {
            CalculatorAction.ClearHistory -> viewModelScope.launch {
                historyRepository.saveHistory(emptyList())
            }
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Scientific -> performScientificOperation(action.operation)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Clear -> _state.update {
                it.copy(
                    number1 = "",
                    number2 = "",
                    operation = null
                )
            }
            is CalculatorAction.Delete -> performDeletion() // Placeholder for AC/Delete logic
            is CalculatorAction.ToggleMode ->
                _state.update {
                    it.copy(
                        mode = if (it.mode == CalculatorMode.Basic) CalculatorMode.Scientific else CalculatorMode.Basic
                    )

            }
            is CalculatorAction.Shift -> _state.update { it.copy(isShifted = !it.isShifted) }
            is CalculatorAction.ShowHistory -> _state.update { it.copy(isHistoryVisible = true) }
            is CalculatorAction.HideHistory -> _state.update { it.copy(isHistoryVisible = false) }
            is CalculatorAction.ToggleAngleUnit -> _state.update {
                it.copy(angleUnit = if(it.angleUnit == AngleUnit.DEG) AngleUnit.RAD else AngleUnit.DEG)
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
        if (operation is ScientificOperation.E) {
            _state.update { it.copy(number1 = Math.E.toString().take(15)) }
            return
        }

        val targetNumber = _state.value.number1.toDoubleOrNull() ?: return

        val result = when (operation) {
            ScientificOperation.Sin ->  {
                val angle = if(_state.value.angleUnit == AngleUnit.DEG) Math.toRadians(targetNumber) else targetNumber
                sin(angle)
            }
            ScientificOperation.Cos ->  {
                val angle = if(_state.value.angleUnit == AngleUnit.DEG) Math.toRadians(targetNumber) else targetNumber
                cos(angle)
            }
            ScientificOperation.Tan ->  {
                val angle = if(_state.value.angleUnit == AngleUnit.DEG) Math.toRadians(targetNumber) else targetNumber
                tan(angle)
            }
            ScientificOperation.Asin ->  {
                val angle = if(_state.value.angleUnit == AngleUnit.DEG) Math.toRadians(targetNumber) else targetNumber
                asin(angle)
            }
            ScientificOperation.Acos ->  {
                val angle = if(_state.value.angleUnit == AngleUnit.DEG) Math.toRadians(targetNumber) else targetNumber
                acos(angle)
            }
            ScientificOperation.Atan ->  {
                val angle = if(_state.value.angleUnit == AngleUnit.DEG) Math.toRadians(targetNumber) else targetNumber
                atan(angle)
            }
            ScientificOperation.Reciprocal -> if (targetNumber != 0.0) 1.0 / targetNumber else Double.NaN
            ScientificOperation.Log -> log10(targetNumber)
            ScientificOperation.Ln -> ln(targetNumber)
            ScientificOperation.Square -> targetNumber.pow(2)
            ScientificOperation.Cube -> targetNumber.pow(3)
            ScientificOperation.EPower -> exp(targetNumber)
            ScientificOperation.TenPower -> 10.0.pow(targetNumber)
            ScientificOperation.Sqrt -> sqrt(targetNumber)
            ScientificOperation.Cbrt -> cbrt(targetNumber)
            ScientificOperation.Factorial -> {
                if (targetNumber >= 0 && targetNumber == targetNumber.toInt().toDouble()) {
                    (1..targetNumber.toInt()).fold(1L) { acc, i -> acc * i }.toDouble()
                } else Double.NaN
            }
            ScientificOperation.Pi -> Math.PI
            ScientificOperation.E -> Math.E
            else -> Double.NaN
        }

        _state.update {
            it.copy(number1 = result.toString().take(15))
        }
    }

    private fun performCalculation() {
        val number1 = _state.value.number1.toDoubleOrNull()
        val number2 = _state.value.number2.toDoubleOrNull()
        val operation = _state.value.operation

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
            is CalculatorOperation.Power -> number1.pow(number2)
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

        val equation = "${_state.value.number1} ${operation?.symbol} ${_state.value.number2} = $resultString"
        viewModelScope.launch {
            val updatedHistory = listOf(equation) + _state.value.history
            historyRepository.saveHistory(updatedHistory)
        }
        _state.update {
            it.copy(
                number1 = resultString,
                number2 = "",
                operation = null,
                history = listOf(equation) + it.history // Prepend the new equation to the history list
            )
        }
    }

    // ViewModel Factory to provide the repository
    class CalculatorViewModelFactory(private val repository: HistoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CalculatorViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
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