package com.hatcorp.icalc.finance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiCalculatorScreen() {
    val viewModel = viewModel<BmiViewModel>()
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("BMI Calculator") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SliderInput(
                label = "Height (cm)",
                value = state.heightCm,
                onValueChange = { viewModel.onAction(BmiAction.HeightChanged(it)) },
                range = 100f..250f,
                steps = 150,
                displayValue = "${state.heightCm.toInt()} cm"
            )
            SliderInput(
                label = "Weight (kg)",
                value = state.weightKg,
                onValueChange = { viewModel.onAction(BmiAction.WeightChanged(it)) },
                range = 30f..150f,
                steps = 120,
                displayValue = "${state.weightKg.toInt()} kg"
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Your BMI", style = MaterialTheme.typography.titleMedium)
                Text(
                    "%.1f".format(state.bmi),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    state.category,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}