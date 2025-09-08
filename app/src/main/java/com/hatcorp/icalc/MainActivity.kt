package com.hatcorp.icalc

import DarkTextSecondary
import LightTextSecondary
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hatcorp.icalc.calculator.*
import com.hatcorp.icalc.ui.theme.ICalcTheme
import com.hatcorp.icalc.calculator.ScientificOperation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ICalcTheme { // Our new theme wrapper
                val viewModel = viewModel<CalculatorViewModel>()
                val state by viewModel.state.collectAsState()
                CalculatorScreen(
                    state = state,
                    onAction = viewModel::onAction
                )
            }
        }
    }
}

@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit
) {
    val displayTextColor = MaterialTheme.colorScheme.onBackground
    val secondaryTextColor = if(isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Display Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = state.number1 + (state.operation?.symbol ?: "") + state.number2,
                        fontSize = 40.sp,
                        color = secondaryTextColor,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                    Text(
                        text = if(state.number1.isEmpty()) "0" else state.number1, // Placeholder for result
                        fontSize = 80.sp,
                        color = displayTextColor,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Animated Scientific Buttons
                AnimatedVisibility(visible = state.mode == CalculatorMode.Scientific) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val scientificRow1 = listOf("(", ")", "sin", "cos", "tan")
                        val scientificRow2 = listOf("log", "ln", "√", "x²", "π")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            scientificRow1.forEach { CalculatorButton(symbol = it, modifier = Modifier.weight(1f)) }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            scientificRow2.forEach { CalculatorButton(symbol = it, modifier = Modifier.weight(1f)) }
                        }
                    }
                }

                // Main Buttons
                val buttonRows = listOf(
                    listOf("AC", "DEL", "%", "÷"),
                    listOf("7", "8", "9", "×"),
                    listOf("4", "5", "6", "-"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "=")
                )

                buttonRows.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { button ->
                            val modifier = Modifier.weight(if (button == "0") 2.1f else 1f)
                            CalculatorButton(
                                symbol = button,
                                modifier = modifier,
                                onClick = { handleButtonClick(button, onAction) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to map button clicks to actions
private fun handleButtonClick(symbol: String, onAction: (CalculatorAction) -> Unit) {
    when (symbol) {
        "AC" -> onAction(CalculatorAction.Clear)
        "DEL" -> onAction(CalculatorAction.Delete)
        "%" -> onAction(CalculatorAction.Operation(CalculatorOperation.Percent))
        "÷" -> onAction(CalculatorAction.Operation(CalculatorOperation.Divide))
        "×" -> onAction(CalculatorAction.Operation(CalculatorOperation.Multiply))
        "-" -> onAction(CalculatorAction.Operation(CalculatorOperation.Subtract))
        "+" -> onAction(CalculatorAction.Operation(CalculatorOperation.Add))
        "." -> onAction(CalculatorAction.Decimal)
        "=" -> onAction(CalculatorAction.Calculate)

        // Add new scientific cases
        "sin" -> onAction(CalculatorAction.Scientific(ScientificOperation.Sin))
        "cos" -> onAction(CalculatorAction.Scientific(ScientificOperation.Cos))
        "tan" -> onAction(CalculatorAction.Scientific(ScientificOperation.Tan))
        "log" -> onAction(CalculatorAction.Scientific(ScientificOperation.Log))
        "ln" -> onAction(CalculatorAction.Scientific(ScientificOperation.Ln))
        "√" -> onAction(CalculatorAction.Scientific(ScientificOperation.Sqrt))
        "x²" -> onAction(CalculatorAction.Scientific(ScientificOperation.Square))
        "π" -> onAction(CalculatorAction.Scientific(ScientificOperation.Pi))

        else -> symbol.toIntOrNull()?.let { onAction(CalculatorAction.Number(it)) }
    }
}


@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val isOperator = "÷×-+==".contains(symbol)
    val isSpecialFunction = "ACDEL%".contains(symbol)
    val isScientificFunction = "()sincostanlogln√x²π".contains(symbol)

    val buttonColor = when {
        isOperator -> MaterialTheme.colorScheme.primary
        isSpecialFunction || isScientificFunction -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f) // Subtle gray like Figma
        else -> MaterialTheme.colorScheme.secondary
    }

    val textColor = MaterialTheme.colorScheme.onPrimary

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(24.dp), // Pill shape
        color = buttonColor,
        onClick = onClick,
        content = {
            Box(contentAlignment = Alignment.Center) {
                if (symbol == "DEL") {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_backspace),
                        contentDescription = "Delete",
                        tint = textColor
                    )
                } else {
                    Text(text = symbol, fontSize = 28.sp, color = textColor)
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun CalculatorScreenPreview() {
    ICalcTheme {
        val state = CalculatorState(number1 = "12,345", mode = CalculatorMode.Scientific)
        CalculatorScreen(state = state, onAction = {})
    }
}