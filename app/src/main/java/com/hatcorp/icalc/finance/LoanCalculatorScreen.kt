package com.hatcorp.icalc.finance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanCalculatorScreen() {
    val viewModel = viewModel<LoanViewModel>()
    val state by viewModel.state.collectAsState()

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
    }


    Scaffold(
        topBar = { TopAppBar(title = { Text("Loan EMI Calculator") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            FinancialInput(
                label = "Principal Amount",
                value = state.principalAmount,
                onValueChange = { viewModel.onAction(LoanAction.PrincipalChanged(it)) }
            )
            FinancialInput(
                label = "Interest Rate (%)",
                value = state.interestRate,
                onValueChange = { viewModel.onAction(LoanAction.RateChanged(it)) }
            )
            FinancialInput(
                label = "Loan Term (Years)",
                value = state.termInYears,
                onValueChange = { viewModel.onAction(LoanAction.TermChanged(it)) }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Display Results
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Monthly Payment (EMI)", style = MaterialTheme.typography.titleMedium)
                Text(currencyFormat.format(state.monthlyPayment), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    ResultCard("Total Interest", currencyFormat.format(state.totalInterest))
                    ResultCard("Total Payment", currencyFormat.format(state.totalPayment))
                }
            }
        }
    }
}


@Composable
fun FinancialInput(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}
@Composable
fun SliderInput(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    displayValue: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(displayValue, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps
        )
    }
}

@Composable
fun ResultCard(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    }
}