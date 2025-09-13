package com.hatcorp.icalc.converter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hatcorp.icalc.AppRoutes

// Data class to hold info for our new grid items
data class UtilityInfo(val name: String, val icon: ImageVector, val route: String)

// Define all our utilities in one place
val utilityCategories = listOf(
    UtilityInfo("Calculator", Icons.Outlined.Calculate, AppRoutes.CALCULATOR), // Added for completeness
    UtilityInfo("Loan", Icons.Outlined.AccountBalance, AppRoutes.LOAN_CALCULATOR),
    UtilityInfo("Investment", Icons.Outlined.TrendingUp, AppRoutes.INVESTMENT_CALCULATOR),
    UtilityInfo("Currency", Icons.Outlined.AttachMoney, AppRoutes.CURRENCY_CONVERTER),
    UtilityInfo("Length", Icons.Outlined.Straighten, "unit_converter/Length"),
    UtilityInfo("Mass", Icons.Outlined.Scale, "unit_converter/Mass"),
    UtilityInfo("BMI", Icons.Outlined.MonitorHeart, AppRoutes.BMI_CALCULATOR),
    UtilityInfo("GST", Icons.Outlined.ReceiptLong, AppRoutes.GST_CALCULATOR),
    UtilityInfo("Date", Icons.Outlined.DateRange, AppRoutes.DATE_CALCULATOR),
    UtilityInfo("Data", Icons.Outlined.DataObject, "unit_converter/Data"),
    UtilityInfo("Speed", Icons.Outlined.Speed, "unit_converter/Speed"),
    UtilityInfo("Temperature", Icons.Outlined.Thermostat, "unit_converter/Temperature")
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterListScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("iCalC Utilities") }) }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(utilityCategories) { utility ->
                UtilityGridItem(info = utility) {
                    // Navigate back for calculator, otherwise navigate to the utility
                    if (utility.route == AppRoutes.CALCULATOR) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(utility.route)
                    }
                }
            }
        }
    }
}

@Composable
fun UtilityGridItem(info: UtilityInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = info.icon, contentDescription = info.name, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = info.name, textAlign = TextAlign.Center)
        }
    }
}