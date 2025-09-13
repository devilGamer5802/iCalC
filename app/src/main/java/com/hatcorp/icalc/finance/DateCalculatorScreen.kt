package com.hatcorp.icalc.finance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCalculatorScreen() {
    val viewModel = viewModel<DateViewModel>()
    val state by viewModel.state.collectAsState()
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    // Show the Date Picker dialogs when their state is true
    if (state.showStartDatePicker) {
        AppDatePicker(
            onDateSelected = { viewModel.onAction(DateAction.StartDateChanged(it)) },
            onDismiss = { viewModel.onAction(DateAction.HideDatePickers) }
        )
    }
    if (state.showEndDatePicker) {
        AppDatePicker(
            onDateSelected = { viewModel.onAction(DateAction.EndDateChanged(it)) },
            onDismiss = { viewModel.onAction(DateAction.HideDatePickers) }
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Date Calculator") }) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { viewModel.onAction(DateAction.ShowStartDatePicker) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("From: ${state.startDate.format(formatter)}")
            }
            Button(
                onClick = { viewModel.onAction(DateAction.ShowEndDatePicker) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("To: ${state.endDate.format(formatter)}")
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Duration:", style = MaterialTheme.typography.headlineSmall)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ResultCard(label = "Years", value = state.durationYears.toString())
                ResultCard(label = "Months", value = state.durationMonths.toString())
                ResultCard(label = "Days", value = state.durationDays.toString())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePicker(onDateSelected: (java.time.LocalDate) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let {
                    val selectedDate = java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    onDateSelected(selectedDate)
                }
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) { Text("Cancel") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}