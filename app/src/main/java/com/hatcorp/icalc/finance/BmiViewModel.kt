package com.hatcorp.icalc.finance

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow

data class BmiState(
    val heightCm: Float = 170f,
    val weightKg: Float = 65f,
    val bmi: Double = 0.0,
    val category: String = ""
)

sealed interface BmiAction {
    data class HeightChanged(val height: Float) : BmiAction
    data class WeightChanged(val weight: Float) : BmiAction
}

class BmiViewModel : ViewModel() {
    private val _state = MutableStateFlow(BmiState())
    val state = _state.asStateFlow()

    init {
        calculate()
    }

    fun onAction(action: BmiAction) {
        when (action) {
            is BmiAction.HeightChanged -> _state.update { it.copy(heightCm = action.height) }
            is BmiAction.WeightChanged -> _state.update { it.copy(weightKg = action.weight) }
        }
        calculate()
    }

    private fun calculate() {
        val heightM = _state.value.heightCm / 100.0
        val weight = _state.value.weightKg.toDouble()

        if (heightM > 0) {
            val bmi = weight / heightM.pow(2)
            val category = when {
                bmi < 18.5 -> "Underweight"
                bmi < 24.9 -> "Normal weight"
                bmi < 29.9 -> "Overweight"
                else -> "Obesity"
            }
            _state.update { it.copy(bmi = bmi, category = category) }
        }
    }
}