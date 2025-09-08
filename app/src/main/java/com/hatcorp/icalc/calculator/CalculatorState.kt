package com.hatcorp.icalc.calculator

enum class CalculatorMode {
    Basic, Scientific
}
data class CalculatorState(
    val number1: String = "",
    val number2: String = "",
    val operation: CalculatorOperation? = null,
    val mode: CalculatorMode = CalculatorMode.Scientific,
    val history: List<String> = emptyList(),
    val isHistoryVisible: Boolean = false
)
