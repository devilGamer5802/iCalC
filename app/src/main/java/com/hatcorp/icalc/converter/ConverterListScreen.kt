package com.hatcorp.icalc.converter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hatcorp.icalc.AppRoutes


// We will add more to this list in future chapters
val converterCategories = listOf(
    "Length", "Mass", "Currency", "Loan", "Investment", "BMI", "GST", "Date", "Data", "Speed", "Temperature"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterListScreen(navController: NavController) { // Added navController parameter
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Converter") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(converterCategories) { category ->
                ConverterCategoryItem(category = category) {
                    when (category) {
                        "Currency" -> navController.navigate(AppRoutes.CURRENCY_CONVERTER)
                        "Loan" -> navController.navigate(AppRoutes.LOAN_CALCULATOR)
                        "Investment" -> navController.navigate(AppRoutes.INVESTMENT_CALCULATOR)
                        "BMI" -> navController.navigate(AppRoutes.BMI_CALCULATOR)
                        "GST" -> navController.navigate(AppRoutes.GST_CALCULATOR)
                        "Date" -> navController.navigate(AppRoutes.DATE_CALCULATOR)
                        else -> {
                            if(listOf("Data", "Speed", "Temperature").contains(category)) {
                                // Placeholder for future chapters
                            } else {
                                navController.navigate("unit_converter/$category")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConverterCategoryItem(
    category: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = category, fontSize = 20.sp, modifier = Modifier.weight(1f))
            // You can add an icon here later if you want
        }
    }
}