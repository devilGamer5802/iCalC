package com.hatcorp.icalc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
//import com.hatcorp.icalc.calculator.CalculatorScreen
import com.hatcorp.icalc.calculator.CalculatorViewModel
import com.hatcorp.icalc.converter.ConverterListScreen

// Defines the routes as constants for type safety
object AppRoutes {
    const val CALCULATOR = "calculator"
    const val CONVERTER_LIST = "converter_list"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    calculatorViewModel: CalculatorViewModel
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.CALCULATOR
    ) {
        composable(AppRoutes.CALCULATOR) {
            CalculatorScreen(
                state = calculatorViewModel.state.collectAsState().value,
                onAction = calculatorViewModel::onAction,
                navController = navController // Pass navController
            )
        }
        composable(AppRoutes.CONVERTER_LIST) {
            ConverterListScreen() // We will create this screen next
        }
    }
}