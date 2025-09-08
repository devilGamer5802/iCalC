package com.hatcorp.icalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hatcorp.icalc.calculator.CalculatorAction
import com.hatcorp.icalc.calculator.CalculatorOperation
import com.hatcorp.icalc.calculator.CalculatorState
import com.hatcorp.icalc.calculator.CalculatorViewModel
import com.hatcorp.icalc.ui.theme.DarkGray
import com.hatcorp.icalc.ui.theme.ICalCTheme
import com.hatcorp.icalc.ui.theme.Orange

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ICalCTheme {
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
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkGray
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                val displayText = state.number1 + (state.operation?.symbol ?: "") + state.number2
                Text(
                    text = displayText.ifEmpty { "0" },
                    fontSize = 80.sp,
                    color = Color.White,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 16.dp),
                    maxLines = 1,
                    fontWeight = FontWeight.Light
                )

                val buttonRows = listOf(
                    listOf("AC", "±", "%", "÷"),
                    listOf("7", "8", "9", "×"),
                    listOf("4", "5", "6", "-"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "=")
                )

                buttonRows.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { button ->
                            CalculatorButton(
                                symbol = button,
                                modifier = Modifier
                                    .weight(if (button == "0") 2f else 1f)
                                    .aspectRatio(if (button == "0") 2f else 1f),
                                onClick = {
                                    when (button) {
                                        "AC" -> onAction(CalculatorAction.Clear)
                                        "±", "%" -> { /* TODO */ }
                                        "÷" -> onAction(CalculatorAction.Operation(CalculatorOperation.Divide))
                                        "×" -> onAction(CalculatorAction.Operation(CalculatorOperation.Multiply))
                                        "-" -> onAction(CalculatorAction.Operation(CalculatorOperation.Subtract))
                                        "+" -> onAction(CalculatorAction.Operation(CalculatorOperation.Add))
                                        "." -> onAction(CalculatorAction.Decimal)
                                        "=" -> onAction(CalculatorAction.Calculate)
                                        else -> onAction(CalculatorAction.Number(button.toInt()))
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val backgroundColor = when (symbol) {
        "AC", "±", "%" -> Color.LightGray
        "÷", "×", "-", "+", "=" -> Orange
        else -> MaterialTheme.colorScheme.secondary
    }

    Surface(
        modifier = modifier.padding(4.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = backgroundColor,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol,
                fontSize = 32.sp,
                color = if(backgroundColor == Orange || backgroundColor == Color.LightGray) Color.White else Color.Black
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CalculatorScreenPreview() {
    ICalCTheme {
        CalculatorScreen(
            state = CalculatorState(), // Added default state
            onAction = {} // Added empty lambda for onAction
        )
    }
}
