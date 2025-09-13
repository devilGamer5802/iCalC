package com.hatcorp.icalc.finance

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val gstSlabs = listOf(5, 12, 18, 28)

data class GstState(
    val amount: String = "1000",
    val selectedSlab: Int = 18,
    val cgst: Double = 0.0,
    val sgst: Double = 0.0,
    val totalAmount: Double = 0.0
)

sealed interface GstAction {
    data class AmountChanged(val amount: String) : GstAction
    data class SlabChanged(val slab: Int) : GstAction
}

class GstViewModel : ViewModel() {
    private val _state = MutableStateFlow(GstState())
    val state = _state.asStateFlow()

    init { calculate() }

    fun onAction(action: GstAction) {
        when(action) {
            is GstAction.AmountChanged -> _state.update { it.copy(amount = action.amount) }
            is GstAction.SlabChanged -> _state.update { it.copy(selectedSlab = action.slab) }
        }
        calculate()
    }

    private fun calculate() {
        val baseAmount = _state.value.amount.toDoubleOrNull() ?: 0.0
        val gstRate = _state.value.selectedSlab / 100.0
        val totalGst = baseAmount * gstRate

        val cgst = totalGst / 2.0
        val sgst = totalGst / 2.0
        val totalAmount = baseAmount + totalGst

        _state.update { it.copy(cgst = cgst, sgst = sgst, totalAmount = totalAmount) }
    }
}