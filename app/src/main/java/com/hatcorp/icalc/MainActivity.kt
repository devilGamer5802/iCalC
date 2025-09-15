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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController // Added import
import com.hatcorp.icalc.calculator.*
import com.hatcorp.icalc.ui.theme.ICalcTheme
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ICalcTheme {
                val viewModel = viewModel<CalculatorViewModel>()
                val navController = rememberNavController() // Defined navController
                AppNavHost(
                    navController = navController, // Used defined navController
                    calculatorViewModel = viewModel // Used correct viewModel variable
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    navController: NavHostController
) {
    val sheetState = rememberModalBottomSheetState()

    // Show the Bottom Sheet when isHistoryVisible is true
    if (state.isHistoryVisible) {
        ModalBottomSheet(
            onDismissRequest = { onAction(CalculatorAction.HideHistory) },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Calculation History", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                if (state.history.isEmpty()) {
                    Text("No history yet.")
                } else {
                    LazyColumn {
                        items(state.history) { equation ->
                            Text(equation, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 4.dp))
                            Divider()
                        }
                    }
                }
            }
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // **FIX 1: Using a root Column to properly distribute vertical space.**
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp) // Add padding for the gesture nav bar
        ) {
            // --- DISPLAY AREA ---
            // **FIX 2: Giving the display a weight of 1 makes it fill all available space.**
            // It's wrapped in a Box to allow for complex alignments inside (e.g., icon top-right, text bottom-right).
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { onAction(CalculatorAction.ToggleMode) }) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Mode",
                            tint = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    IconButton(onClick = { onAction(CalculatorAction.ShowHistory) }) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "History",
                            tint = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    // THE NEW NAVIGATION BUTTON
                    IconButton(onClick = { navController.navigate(AppRoutes.CONVERTER_LIST) }) {
                        Icon(
                            imageVector = Icons.Outlined.Widgets, // A much better icon for "other tools"
                            contentDescription = "Change Mode",
                            tint = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd) // This aligns the text column to the bottom-right of the Box
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = state.number1 + (state.operation?.symbol ?: "") + state.number2,
                        fontSize = 40.sp,
                        color = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.number1.ifEmpty { "0" },
                        fontSize = 80.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        fontWeight = FontWeight.Light,
                        lineHeight = 80.sp
                    )
                }
            }

            // --- BUTTON PAD ---
            // This Column now sits naturally at the bottom because the display area took up the rest of the space.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnimatedVisibility(visible = state.mode == CalculatorMode.Scientific) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val sinText = if (state.isShifted) "sin⁻¹" else "sin"
                        val cosText = if (state.isShifted) "cos⁻¹" else "cos"
                        val tanText = if (state.isShifted) "tan⁻¹" else "tan"
                        val lnText = if (state.isShifted) "eˣ" else "ln"
                        val logText = if (state.isShifted) "10ˣ" else "log"
                        val sqrtText = if (state.isShifted) "∛" else "√"
                        val squareText = if (state.isShifted) "x³" else "x²"
                        val piText = if (state.isShifted) "e" else "π"
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // The SHIFT button itself
                            CalculatorButton(symbol = "2nd", modifier = Modifier.weight(1f), isShiftActive = state.isShifted, onClick = { onAction(CalculatorAction.Shift) })
                            CalculatorButton(symbol = sqrtText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(sqrtText, onAction) })
                            CalculatorButton(symbol = squareText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(squareText, onAction) })
                            CalculatorButton(symbol = piText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(piText, onAction) })
                            CalculatorButton(symbol = "x!", modifier = Modifier.weight(1f), onClick = { handleButtonClick("x!", onAction) })
                        }

                        // Other scientific rows
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CalculatorButton(symbol = sinText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(sinText, onAction) })
                            CalculatorButton(symbol = cosText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(cosText, onAction) })
                            CalculatorButton(symbol = tanText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(tanText, onAction) })
                            CalculatorButton(symbol = lnText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(lnText, onAction) })
                            CalculatorButton(symbol = logText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(logText, onAction) })
                        }
                    }
                }
            }

                val buttonRows = listOf(
                    listOf("AC", "DEL", "%", "÷"),
                    listOf("7", "8", "9", "×"),
                    listOf("4", "5", "6", "-"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "=")
                )

                buttonRows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { button ->
                            val weight = if (button == "0") 2f else 1f
                            val aspectRatio = if (button == "0") 2f else 1f

                            CalculatorButton(
                                symbol = button,
                                modifier = Modifier.weight(weight),
                                aspectRatio = aspectRatio,
                                onClick = { handleButtonClick(button, onAction) }
                            )
                        }
                    }
                }
            }
        }
    }

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 1f, // Accept aspect ratio as a parameter
    onClick: () -> Unit = {},
    isShiftActive: Boolean = false,
) {
    val isOperator = "÷×-+=".contains(symbol)
    val isSpecialFunction = "ACDEL%".contains(symbol)

    val buttonColor = when {
        isOperator -> MaterialTheme.colorScheme.primary
        isSpecialFunction -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.secondary
    }

    val contentColor = when {
        isShiftActive -> MaterialTheme.colorScheme.tertiary
        isOperator -> MaterialTheme.colorScheme.onPrimary
        isSpecialFunction -> MaterialTheme.colorScheme.onBackground
        else -> MaterialTheme.colorScheme.onSecondary
    }

    Surface(
        modifier = modifier
            .aspectRatio(aspectRatio) // Use the passed-in aspect ratio
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = buttonColor,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (symbol == "DEL") {
                Icon(
                    painter = painterResource(id = R.drawable.ic_backspace),
                    contentDescription = "Delete",
                    tint = contentColor
                )
            } else {
                Text(
                    text = symbol,
                    fontSize = 28.sp,
                    color = contentColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ... handleButtonClick and Previews remain the same ...
private fun handleButtonClick(symbol: String, onAction: (CalculatorAction) -> Unit) {
    // Unchanged
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
@Preview(showBackground = true, name = "Light Mode Preview")
@Composable
fun CalculatorScreenPreviewLight() {
    ICalcTheme(darkTheme = false) {
        val state = CalculatorState(number1 = "12,345", mode = CalculatorMode.Scientific, number2 = "*2", operation = CalculatorOperation.Multiply)
        CalculatorScreen(state = state, onAction = {}, navController = rememberNavController()) // Added navController
    }
}
@Preview(showBackground = true, name = "Dark Mode Preview")
@Composable
fun CalculatorScreenPreviewDark() {
    ICalcTheme(darkTheme = true) {
        val state = CalculatorState(number1 = "12,345", mode = CalculatorMode.Scientific, number2 = "*2", operation = CalculatorOperation.Multiply)
        CalculatorScreen(state = state, onAction = {}, navController = rememberNavController()) // Added navController
    }
}
