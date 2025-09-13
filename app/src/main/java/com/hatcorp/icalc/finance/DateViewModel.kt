package com.hatcorp.icalc.finance

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.Period

data class DateState(
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now().plusDays(1),
    val durationYears: Int = 0,
    val durationMonths: Int = 0,
    val durationDays: Int = 0,
    val showStartDatePicker: Boolean = false,
    val showEndDatePicker: Boolean = false
)

sealed interface DateAction {
    data class StartDateChanged(val date: LocalDate) : DateAction
    data class EndDateChanged(val date: LocalDate) : DateAction
    data object ShowStartDatePicker : DateAction
    data object ShowEndDatePicker : DateAction
    data object HideDatePickers : DateAction
}

class DateViewModel : ViewModel() {
    private val _state = MutableStateFlow(DateState())
    val state = _state.asStateFlow()

    init {
        calculateDuration()
    }

    fun onAction(action: DateAction) {
        when(action) {
            is DateAction.StartDateChanged -> _state.update { it.copy(startDate = action.date) }
            is DateAction.EndDateChanged -> _state.update { it.copy(endDate = action.date) }
            is DateAction.ShowStartDatePicker -> _state.update { it.copy(showStartDatePicker = true) }
            is DateAction.ShowEndDatePicker -> _state.update { it.copy(showEndDatePicker = true) }
            is DateAction.HideDatePickers -> _state.update { it.copy(showStartDatePicker = false, showEndDatePicker = false) }
        }
        calculateDuration()
    }

    private fun calculateDuration() {
        val period = Period.between(_state.value.startDate, _state.value.endDate)
        _state.update {
            it.copy(
                durationYears = period.years,
                durationMonths = period.months,
                durationDays = period.days
            )
        }
    }
}