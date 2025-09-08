package com.hatcorp.icalc.converter

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(viewModel: UnitConverterViewModel) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.category.name) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UnitRow(
                value = state.fromValue,
                onValueChange = { viewModel.onAction(ConverterAction.FromValueChanged(it)) },
                selectedUnit = state.fromUnit,
                onUnitChange = { viewModel.onAction(ConverterAction.FromUnitChanged(it)) },
                units = state.category.units,
                label = "From"
            )

            IconButton(onClick = { viewModel.onAction(ConverterAction.SwapUnits) }) {
                Icon(Icons.Default.SwapVert, contentDescription = "Swap Units")
            }

            UnitRow(
                value = state.toValue,
                onValueChange = { /* To value is read-only */ },
                selectedUnit = state.toUnit,
                onUnitChange = { viewModel.onAction(ConverterAction.ToUnitChanged(it)) },
                units = state.category.units,
                label = "To",
                isReadOnly = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitRow(
    value: String,
    onValueChange: (String) -> Unit,
    selectedUnit: UnitInfo,
    onUnitChange: (UnitInfo) -> Unit,
    units: List<UnitInfo>,
    label: String,
    isReadOnly: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            readOnly = isReadOnly
        )
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            TextField(
                readOnly = true,
                value = selectedUnit.name,
                onValueChange = { },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text("${unit.name} (${unit.symbol})") },
                        onClick = {
                            onUnitChange(unit)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}