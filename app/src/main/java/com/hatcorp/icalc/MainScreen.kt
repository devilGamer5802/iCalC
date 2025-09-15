package com.hatcorp.icalc

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hatcorp.icalc.calculator.CalculatorViewModel
import com.hatcorp.icalc.converter.ConverterListScreen

@Composable
fun MainScreen(
    navController: NavHostController,
    calculatorViewModel: CalculatorViewModel
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Calculator", "Converter")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        when (tabIndex) {
            0 -> CalculatorScreen(
                state = calculatorViewModel.state.collectAsState().value,
                onAction = calculatorViewModel::onAction,
                navController = navController
            )
            1 -> ConverterListScreen(navController = navController)
        }
    }
}