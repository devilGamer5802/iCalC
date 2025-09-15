package com.hatcorp.icalc.finance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GstCalculatorScreen(navController: NavController) {
    val viewModel = viewModel<GstViewModel>()
    val state by viewModel.state.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(java.util.Locale("en", "IN"))

    Scaffold(topBar = { TopAppBar(title = { Text("GST Calculator") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = state.amount,
                onValueChange = { viewModel.onAction(GstAction.AmountChanged(it)) },
                label = { Text("Base Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Text("GST Slab:", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.selectableGroup().fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                gstSlabs.forEach { slab ->
                    Row(
                        Modifier.selectable(
                            selected = (slab == state.selectedSlab),
                            onClick = { viewModel.onAction(GstAction.SlabChanged(slab)) },
                            role = Role.RadioButton
                        ).padding(vertical = 8.dp)
                    ) {
                        RadioButton(selected = (slab == state.selectedSlab), onClick = null)
                        Text(text = "$slab%", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            ResultCard("CGST", currencyFormat.format(state.cgst))
            Spacer(Modifier.height(8.dp))
            ResultCard("SGST", currencyFormat.format(state.sgst))
            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(16.dp))
            ResultCard("Total Amount (incl. GST)", currencyFormat.format(state.totalAmount))
        }
    }
}