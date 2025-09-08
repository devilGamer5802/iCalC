package com.hatcorp.icalc.currency

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hatcorp.icalc.converter.UnitRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen() {
    val viewModel = viewModel<CurrencyViewModel>()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Currency Converter") }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.error != null) {
                Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
            } else {
                // Here we reuse the UnitRow Composable idea, adapted for this screen
                // In a real project, we would make a more generic component
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // TODO: Build the two UnitRow-like components for currency
                    // and the swap button. This UI will be very similar
                    // to the UnitConverterScreen.
                    Text("Currency Converter UI will go here.")
                }
            }
        }
    }
}