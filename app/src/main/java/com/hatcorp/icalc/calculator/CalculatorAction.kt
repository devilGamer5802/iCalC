package com.hatcorp.icalc.calculator

sealed interface CalculatorAction {
    data class Number(val number: Int) : CalculatorAction
    data object Clear : CalculatorAction
    data object Delete : CalculatorAction // We'll map 'AC' to this for now
    data object Decimal : CalculatorAction
    data object Calculate : CalculatorAction
    data class Operation(val operation: CalculatorOperation) : CalculatorAction
}