package com.hatcorp.icalc.calculator

sealed class ScientificOperation(val symbol: String) {
    data object Sin : ScientificOperation("sin")
    data object Cos : ScientificOperation("cos")
    data object Tan : ScientificOperation("tan")
    data object Log : ScientificOperation("log")
    data object Ln : ScientificOperation("ln")
    data object Sqrt : ScientificOperation("√")
    data object Square : ScientificOperation("x²")
    data object Pi : ScientificOperation("π")
    // Parentheses are special and will be handled separately
}
sealed interface CalculatorAction {
    data class Number(val number: Int) : CalculatorAction
    data object Clear : CalculatorAction
    data object Delete : CalculatorAction // We'll map 'AC' to this for now
    data object Decimal : CalculatorAction
    data object Calculate : CalculatorAction
    data class Operation(val operation: CalculatorOperation) : CalculatorAction

    data object ToggleMode : CalculatorAction

    data class Scientific(val operation: ScientificOperation) : CalculatorAction
}