package com.hatcorp.icalc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hatcorp.icalc.calculator.CalculatorViewModel
import com.hatcorp.icalc.converter.ConverterListScreen
import com.hatcorp.icalc.whiteboard.WhiteboardScreen

@Composable
fun MainScreen(
    navController: NavHostController,
    calculatorViewModel: CalculatorViewModel
) {
    var selectedIndex by remember { mutableStateOf(0) }
    var tabIndex by remember { mutableStateOf(0) }
    val pages = listOf("Calculator", "Converter","Whiteboard")

    Scaffold(
        topBar = {
            SegmentedControl(
                items = pages,
                selectedIndex = selectedIndex,
                onIndexSelected = { selectedIndex = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 48.dp) // Proper padding
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedIndex) {
                0 -> CalculatorScreen( // The Calculator screen no longer needs to pass NavController
                    state = calculatorViewModel.state.collectAsState().value,
                    onAction = calculatorViewModel::onAction
                )
                1 -> ConverterListScreen(navController = navController)
                2 -> WhiteboardScreen()
            }
        }
    }
}

@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onIndexSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onIndexSelected(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}