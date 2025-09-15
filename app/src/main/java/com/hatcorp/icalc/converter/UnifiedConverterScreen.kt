package com.hatcorp.icalc.converter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hatcorp.icalc.converter.components.ConversionRow
import com.hatcorp.icalc.converter.components.ConverterKeypad
import com.hatcorp.icalc.currency.CurrencyState
import com.hatcorp.icalc.currency.CurrencyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedConverterScreen(
    navController: NavController,
    categoryName: String?
) {
    val isCurrency = categoryName == "Currency"

    if (isCurrency) {
        val currencyViewModel = viewModel<CurrencyViewModel>()
        val currencyState by currencyViewModel.state.collectAsState()
        CurrencyScreenContent(
            navController = navController,
            state = currencyState,
            onAction = currencyViewModel::onAction
        )
    } else {
        val unitViewModel = viewModel<UnitConverterViewModel>()
        val unitState by unitViewModel.state.collectAsState()
        UnitScreenContent(
            navController = navController,
            state = unitState,
            onAction = unitViewModel::onAction
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreenContent(
    navController: NavController,
    state: CurrencyState,
    onAction: (ConverterAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Currency") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Column(Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.weight(1f)) {
                        ConversionRow(
                            unit = state.fromUnit,
                            value = state.fromValue,
                            isActive = state.activeInput == ActiveInput.FROM,
                            onActive = { onAction(ConverterAction.SetActiveInput(ActiveInput.FROM)) },
                            isMenuExpanded = state.expandedMenu == ExpandedMenu.FROM,
                            onMenuToggle = { onAction(ConverterAction.ToggleMenu(ExpandedMenu.FROM)) },
                            onUnitSelected = {
                                onAction(ConverterAction.FromUnitChanged(it))
                                onAction(ConverterAction.CloseMenus)
                            },
                            availableUnits = state.availableUnits
                        )
                        Divider()
                        ConversionRow(
                            unit = state.toUnit,
                            value = state.toValue,
                            isActive = state.activeInput == ActiveInput.TO,
                            onActive = { onAction(ConverterAction.SetActiveInput(ActiveInput.TO)) },
                            isMenuExpanded = state.expandedMenu == ExpandedMenu.TO,
                            onMenuToggle = { onAction(ConverterAction.ToggleMenu(ExpandedMenu.TO)) },
                            onUnitSelected = {
                                onAction(ConverterAction.ToUnitChanged(it))
                                onAction(ConverterAction.CloseMenus)
                            },
                            availableUnits = state.availableUnits
                        )
                    }
                    ConverterKeypad(onKeyPress = { key -> onAction(ConverterAction.KeyPress(key)) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitScreenContent(
    navController: NavController,
    state: UnitConverterState,
    onAction: (ConverterAction) -> Unit
) {
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
                    onActive = { onAction(ConverterAction.SetActiveInput(ActiveInput.FROM)) },
                    isMenuExpanded = state.expandedMenu == ExpandedMenu.FROM,
                    onMenuToggle = { onAction(ConverterAction.ToggleMenu(ExpandedMenu.FROM)) },
                    onUnitSelected = {
                        onAction(ConverterAction.FromUnitChanged(it))
                        onAction(ConverterAction.CloseMenus)
                    },
                    availableUnits = state.category.units
                )
                Divider()
                ConversionRow(
                    unit = state.toUnit,
                    value = state.toValue,
                    isActive = state.activeInput == ActiveInput.TO,
                    onActive = { onAction(ConverterAction.SetActiveInput(ActiveInput.TO)) },
                    isMenuExpanded = state.expandedMenu == ExpandedMenu.TO,
                    onMenuToggle = { onAction(ConverterAction.ToggleMenu(ExpandedMenu.TO)) },
                    onUnitSelected = {
                        onAction(ConverterAction.ToUnitChanged(it))
                        onAction(ConverterAction.CloseMenus)
                    },
                    availableUnits = state.category.units
                )
            }
            ConverterKeypad(onKeyPress = { key ->
                onAction(ConverterAction.KeyPress(key))
            })
        }
    }
}