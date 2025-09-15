package com.hatcorp.icalc.calculator

sealed class ScientificOperation(val symbol: String) {
    data object Sin : ScientificOperation("sin")
    data object Cos : ScientificOperation("cos")
    data object Tan : ScientificOperation("tan")
    data object Asin : ScientificOperation("sin⁻¹")
    data object Acos : ScientificOperation("cos⁻¹")
    data object Atan : ScientificOperation("tan⁻¹")
    data object Log : ScientificOperation("log")
    data object Ln : ScientificOperation("ln")
    data object Square : ScientificOperation("x²")
    data object Cube : ScientificOperation("x³")
    data object PowerY : ScientificOperation("xʸ") // For the future; needs 2 operands
    data object EPower : ScientificOperation("eˣ")
    data object TenPower : ScientificOperation("10ˣ")
    data object Sqrt : ScientificOperation("√")
    data object Cbrt : ScientificOperation("∛")
    data object Factorial : ScientificOperation("x!")
    data object Pi : ScientificOperation("π")
    data object E : ScientificOperation("e")
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

    data object ShowHistory : CalculatorAction

    data object HideHistory : CalculatorAction

    data object Shift : CalculatorAction
}