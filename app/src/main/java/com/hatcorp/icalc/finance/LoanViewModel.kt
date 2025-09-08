package com.hatcorp.icalc.finance

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow

data class LoanState(
    val principalAmount: Float = 10000f,
    val interestRate: Float = 7.5f,
    val termInYears: Int = 5,
    val monthlyPayment: Double = 0.0,
    val totalInterest: Double = 0.0,
    val totalPayment: Double = 0.0
)

sealed interface LoanAction {
    data class PrincipalChanged(val amount: Float) : LoanAction
    data class RateChanged(val rate: Float) : LoanAction
    data class TermChanged(val term: Int) : LoanAction
}

class LoanViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoanState())
    val state = _state.asStateFlow()

    init {
        calculate()
    }

    fun onAction(action: LoanAction) {
        when (action) {
            is LoanAction.PrincipalChanged -> _state.update { it.copy(principalAmount = action.amount) }
            is LoanAction.RateChanged -> _state.update { it.copy(interestRate = action.rate) }
            is LoanAction.TermChanged -> _state.update { it.copy(termInYears = action.term) }
        }
        calculate()
    }

    private fun calculate() {
        val p = _state.value.principalAmount.toDouble()
        val r = (_state.value.interestRate / 100) / 12 // Monthly interest rate
        val n = _state.value.termInYears * 12 // Total number of payments

        if (r > 0) {
            val emi = p * r * (1 + r).pow(n) / ((1 + r).pow(n) - 1)
            val totalPayment = emi * n
            val totalInterest = totalPayment - p

            _state.update {
                it.copy(
                    monthlyPayment = emi,
                    totalPayment = totalPayment,
                    totalInterest = totalInterest
                )
            }
        } else { // Handle 0% interest case
            val emi = p / n
            _state.update {
                it.copy(
                    monthlyPayment = emi,
                    totalPayment = p,
                    totalInterest = 0.0
                )
            }
        }
    }
}