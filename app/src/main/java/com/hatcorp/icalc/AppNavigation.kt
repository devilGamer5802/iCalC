package com.hatcorp.icalc

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hatcorp.icalc.converter.ConverterListScreen
import com.hatcorp.icalc.converter.UnitConverterScreen // New screen
import com.hatcorp.icalc.converter.UnitConverterViewModel // New ViewModel
// import com.hatcorp.icalc.calculator.CalculatorScreen // Commented out potentially incorrect import
// CalculatorScreen is likely in com.hatcorp.icalc package (MainActivity.kt)
import com.hatcorp.icalc.calculator.CalculatorViewModel
import com.hatcorp.icalc.converter.UnifiedConverterScreen
//import com.hatcorp.icalc.finance.InvestmentCalculatorScreen
import com.hatcorp.icalc.finance.LoanCalculatorScreen
import com.hatcorp.icalc.finance.BmiCalculatorScreen
import com.hatcorp.icalc.finance.DateCalculatorScreen
//import com.hatcorp.icalc.finance.DateCalculatorScreen
import com.hatcorp.icalc.finance.GstCalculatorScreen
// Removed duplicate import for com.hatcorp.icalc.converter.ConverterListScreen as it's already imported above

// Defines the routes as constants for type safety
object AppRoutes {
    const val CALCULATOR = "calculator"
    const val CONVERTER_LIST = "converter_list"
    const val UNIT_CONVERTER = "unit_converter/{category}"
    const val CURRENCY_CONVERTER = "currency_converter" // New route
    const val LOAN_CALCULATOR = "loan_calculator"
    const val INVESTMENT_CALCULATOR = "investment_calculator"
    const val BMI_CALCULATOR = "bmi_calculator"
    const val GST_CALCULATOR = "gst_calculator"
    const val DATE_CALCULATOR = "date_calculator"
    const val UNIFIED_CONVERTER = "unified_converter/{category}"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
) {
    val application = LocalContext.current.applicationContext as ICalcApplication
    val calculatorViewModel: CalculatorViewModel = viewModel(
        factory = CalculatorViewModel.CalculatorViewModelFactory(application.historyRepository)
    )
    NavHost(
        navController = navController,
        startDestination = AppRoutes.CALCULATOR
    ) {

        composable(AppRoutes.CALCULATOR) {
            MainScreen(
                navController = navController,
                calculatorViewModel = calculatorViewModel
            )
        }
        composable(AppRoutes.CONVERTER_LIST) {
            ConverterListScreen(navController = navController) // We will create this screen next
        }
        composable(AppRoutes.UNIT_CONVERTER) { backStackEntry ->
            val viewModel = viewModel<UnitConverterViewModel>(
                // The viewModel will now automatically receive the category from the navigation arguments
                // factory = // We might need a factory later, but SavedStateHandle should work by default // Removed incomplete factory
            )
            UnitConverterScreen(viewModel = viewModel)
        }
        composable(AppRoutes.CURRENCY_CONVERTER) {
            //CurrencyConverterScreen()
        }
        composable(AppRoutes.LOAN_CALCULATOR) {
            LoanCalculatorScreen(navController = navController)
        }
        composable(AppRoutes.INVESTMENT_CALCULATOR) {
            //InvestmentCalculatorScreen()
        }
        composable(AppRoutes.BMI_CALCULATOR) { BmiCalculatorScreen(navController = navController) }
        composable(AppRoutes.GST_CALCULATOR) { GstCalculatorScreen(navController = navController) }
        composable(AppRoutes.DATE_CALCULATOR) { DateCalculatorScreen(navController = navController) }
        composable(AppRoutes.UNIFIED_CONVERTER) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category")
            UnifiedConverterScreen(navController = navController, categoryName = category)
        }
    }
}
