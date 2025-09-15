package com.hatcorp.icalc.converter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hatcorp.icalc.converter.components.ConversionRow
import com.hatcorp.icalc.converter.components.ConverterKeypad
import com.hatcorp.icalc.currency.CurrencyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedConverterScreen(
    navController: NavController,
    categoryName: String?
) {
    val isCurrency = categoryName == "Currency"

    if (isCurrency) {
        val viewModel = viewModel<CurrencyViewModel>()
        // The Currency screen UI can be built here using the same components
        Text("Currency Converter UI to be implemented here", modifier = Modifier.padding(16.dp))
    } else {
        val viewModel = viewModel<UnitConverterViewModel>()
        val state by viewModel.state.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(state.category.name) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(Modifier.padding(padding).fillMaxSize()) {
                Column(modifier = Modifier.weight(1f)) {
                    ConversionRow(
                        unit = state.fromUnit,
                        value = state.fromValue,
                        isActive = state.activeInput == ActiveInput.FROM,
                        onActive = { viewModel.onAction(ConverterAction.SetActiveInput(ActiveInput.FROM)) },
                        isMenuExpanded = state.expandedMenu == ExpandedMenu.FROM,
                        onMenuToggle = { viewModel.onAction(ConverterAction.ToggleMenu(ExpandedMenu.FROM)) },
                        onUnitSelected = {
                            viewModel.onAction(ConverterAction.FromUnitChanged(it))
                            viewModel.onAction(ConverterAction.CloseMenus)
                        },
                        availableUnits = state.category.units,
                    )
                    Divider()
                    ConversionRow(
                        unit = state.toUnit,
                        value = state.toValue,
                        isActive = state.activeInput == ActiveInput.TO,
                        onActive = { viewModel.onAction(ConverterAction.SetActiveInput(ActiveInput.TO)) },
                        isMenuExpanded = state.expandedMenu == ExpandedMenu.TO,
                        onMenuToggle = { viewModel.onAction(ConverterAction.ToggleMenu(ExpandedMenu.TO)) },
                        onUnitSelected = {
                            viewModel.onAction(ConverterAction.ToUnitChanged(it))
                            viewModel.onAction(ConverterAction.CloseMenus)
                        },
                        availableUnits = state.category.units,
                    )
                }
                ConverterKeypad(onKeyPress = { key ->
                    viewModel.onAction(ConverterAction.KeyPress(key))
                })
            }
        }
    }
}