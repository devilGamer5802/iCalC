package com.hatcorp.icalc.currency

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen() {
    val viewModel = viewModel<CurrencyViewModel>()
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Currency Converter") }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.error != null) {
                Text(text = state.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            } else {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CurrencyRow(
                        value = state.fromValue,
                        onValueChange = { viewModel.onAction(CurrencyAction.AmountChange(it)) },
                        selectedCurrency = state.fromCurrency,
                        onCurrencyChange = { viewModel.onAction(CurrencyAction.FromCurrencyChange(it)) },
                        currencies = state.currencies,
                        label = "From"
                    )
                    IconButton(onClick = { viewModel.onAction(CurrencyAction.SwapCurrencies) }) {
                        Icon(Icons.Default.SwapVert, contentDescription = "Swap Currencies")
                    }
                    CurrencyRow(
                        value = state.toValue,
                        onValueChange = { /* Read Only */ },
                        selectedCurrency = state.toCurrency,
                        onCurrencyChange = { viewModel.onAction(CurrencyAction.ToCurrencyChange(it)) },
                        currencies = state.currencies,
                        label = "To",
                        isReadOnly = true
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyRow(
    value: String,
    onValueChange: (String) -> Unit,
    selectedCurrency: String,
    onCurrencyChange: (String) -> Unit,
    currencies: List<String>,
    label: String,
    isReadOnly: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            readOnly = isReadOnly
        )
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextField(
                readOnly = true,
                value = selectedCurrency,
                onValueChange = { },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().width(120.dp)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                currencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency) },
                        onClick = {
                            onCurrencyChange(currency)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}