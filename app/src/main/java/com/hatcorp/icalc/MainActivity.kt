package com.hatcorp.icalc

import DarkTextSecondary
import LightTextSecondary
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hatcorp.icalc.calculator.*
import com.hatcorp.icalc.ui.theme.ICalcTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ICalcTheme {
                val navController = rememberNavController()
                val calculatorViewModel = viewModel<CalculatorViewModel>()

                AppNavHost(
                    navController = navController,
                    calculatorViewModel = calculatorViewModel
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// --- Main Screen Composables (Portrait/Landscape) ---
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    navController: NavHostController
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val sheetState = rememberModalBottomSheetState()

    if (state.isHistoryVisible) {
        ModalBottomSheet(
            onDismissRequest = { onAction(CalculatorAction.HideHistory) },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text("Calculation History", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                if (state.history.isEmpty()) {
                    Text("No history yet.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(state.history) { equation ->
                            Text(equation, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 4.dp))
                            HorizontalDivider()
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
        if (isLandscape) {
            LandscapeCalculatorLayout(state, onAction)
        } else {
            PortraitCalculatorLayout(state, onAction, navController)
        }
    }
}

@Composable
fun PortraitCalculatorLayout(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onAction(CalculatorAction.ToggleMode) }) {
                    Icon(Icons.Default.SwapHoriz, "Switch Mode", tint = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary, modifier = Modifier.size(32.dp))
                }
                Row {
                    IconButton(onClick = { onAction(CalculatorAction.ShowHistory) }) {
                        Icon(Icons.Outlined.History, "History", tint = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary, modifier = Modifier.size(32.dp))
                    }
                    IconButton(onClick = { navController.navigate(AppRoutes.CONVERTER_LIST) }) {
                        Icon(Icons.Outlined.Widgets, "Utilities", tint = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary, modifier = Modifier.size(32.dp))
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomEnd).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = state.number1 + (state.operation?.symbol ?: "") + state.number2,
                    fontSize = 40.sp, color = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary,
                    textAlign = TextAlign.End, maxLines = 1
                )
                Text(
                    text = if (state.number2.isNotBlank()) state.number2 else state.number1.ifEmpty { "0" },
                    fontSize = 80.sp, color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.End, maxLines = 1, fontWeight = FontWeight.Light
                )
            }
        }
        ButtonPad(state = state, onAction = onAction)
    }
}

@Composable
fun LandscapeCalculatorLayout(state: CalculatorState, onAction: (CalculatorAction) -> Unit) {
    Row(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = state.number1 + (state.operation?.symbol ?: "") + state.number2,
                fontSize = 24.sp, color = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary, maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.number2.ifBlank { state.number1.ifEmpty { "0" } },
                fontSize = 48.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Light, maxLines = 1
            )
        }
        Box(modifier = Modifier.weight(1.5f).fillMaxHeight()) {
            ButtonPad(state = state, onAction = onAction)
        }
    }
}

// -----------------------------------------------------------------------------
// --- Calculator Components & Logic ---
// -----------------------------------------------------------------------------

@Composable
fun ButtonPad(state: CalculatorState, onAction: (CalculatorAction) -> Unit) {
    val buttonSpacing = 10.dp

    Column(verticalArrangement = Arrangement.spacedBy(buttonSpacing)) {
        AnimatedVisibility(visible = state.mode == CalculatorMode.Scientific) {
            Column(verticalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                val radText = if (state.angleUnit == AngleUnit.DEG) "deg" else "rad"
                Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                    CalculatorButton("2nd", Modifier.weight(1f), isShiftActive = state.isShifted, onClick = { onAction(CalculatorAction.Shift) })
                    CalculatorButton(radText, Modifier.weight(1f), onClick = { onAction(CalculatorAction.ToggleAngleUnit) })
                    CalculatorButton("sin", Modifier.weight(1f), onClick = { handleButtonClick("sin", onAction) })
                    CalculatorButton("cos", Modifier.weight(1f), onClick = { handleButtonClick("cos", onAction) })
                    CalculatorButton("tan", Modifier.weight(1f), onClick = { handleButtonClick("tan", onAction) })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                    CalculatorButton("xʸ", Modifier.weight(1f), onClick = { handleButtonClick("xʸ", onAction) })
                    CalculatorButton("log", Modifier.weight(1f), onClick = { handleButtonClick("log", onAction) })
                    CalculatorButton("ln", Modifier.weight(1f), onClick = { handleButtonClick("ln", onAction) })
                    CalculatorButton("(", Modifier.weight(1f), onClick = { /* Not Implemented */ })
                    CalculatorButton(")", Modifier.weight(1f), onClick = { /* Not Implemented */ })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                    CalculatorButton("√", Modifier.weight(1f), onClick = { handleButtonClick("√", onAction) })
                    CalculatorButton("AC", Modifier.weight(1f), onClick = { handleButtonClick("AC", onAction) })
                    CalculatorButton("DEL", Modifier.weight(1f), onClick = { handleButtonClick("DEL", onAction) })
                    CalculatorButton("%", Modifier.weight(1f), onClick = { handleButtonClick("%", onAction) })
                    CalculatorButton("÷", Modifier.weight(1f), isOperator = true, onClick = { handleButtonClick("÷", onAction) })
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalculatorButton("1/x", Modifier.weight(1f), onClick = { handleButtonClick("1/x", onAction) })
            CalculatorButton("7", Modifier.weight(1f), onClick = { handleButtonClick("7", onAction) })
            CalculatorButton("8", Modifier.weight(1f), onClick = { handleButtonClick("8", onAction) })
            CalculatorButton("9", Modifier.weight(1f), onClick = { handleButtonClick("9", onAction) })
            CalculatorButton("×", Modifier.weight(1f), isOperator = true, onClick = { handleButtonClick("×", onAction) })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalculatorButton("π", Modifier.weight(1f), onClick = { handleButtonClick("π", onAction) })
            CalculatorButton("4", Modifier.weight(1f), onClick = { handleButtonClick("4", onAction) })
            CalculatorButton("5", Modifier.weight(1f), onClick = { handleButtonClick("5", onAction) })
            CalculatorButton("6", Modifier.weight(1f), onClick = { handleButtonClick("6", onAction) })
            CalculatorButton("-", Modifier.weight(1f), isOperator = true, onClick = { handleButtonClick("-", onAction) })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalculatorButton("e", Modifier.weight(1f), onClick = { handleButtonClick("e", onAction) })
            CalculatorButton("1", Modifier.weight(1f), onClick = { handleButtonClick("1", onAction) })
            CalculatorButton("2", Modifier.weight(1f), onClick = { handleButtonClick("2", onAction) })
            CalculatorButton("3", Modifier.weight(1f), onClick = { handleButtonClick("3", onAction) })
            CalculatorButton("+", Modifier.weight(1f), isOperator = true, onClick = { handleButtonClick("+", onAction) })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalculatorButton("±", Modifier.weight(1f), onClick = { /* TODO: Implement ToggleSign */ })
            CalculatorButton("0", modifier = Modifier.weight(1f).weight(2.05f), onClick = { handleButtonClick("0", onAction) })
            CalculatorButton(".", Modifier.weight(1f), onClick = { handleButtonClick(".", onAction) })
            CalculatorButton("=", Modifier.weight(1f), isOperator = true, onClick = { handleButtonClick("=", onAction) })
        }
    }
}

@Composable
fun CalculatorButton(symbol: String, modifier: Modifier, isOperator: Boolean = false, isShiftActive: Boolean = false, onClick: () -> Unit) {
    val buttonColor = when {
        isShiftActive -> MaterialTheme.colorScheme.tertiaryContainer
        isOperator -> MaterialTheme.colorScheme.primary
        "ACDEL%±".contains(symbol) || symbol.length > 1 -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.secondary
    }
    val contentColor = when {
        isShiftActive -> MaterialTheme.colorScheme.onTertiaryContainer
        isOperator -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }
    Surface(
        modifier = modifier.aspectRatio(if (symbol == "0") 2f else 1f).fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), color = buttonColor, onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (symbol == "DEL") {
                Icon(painterResource(R.drawable.ic_backspace), "Delete", tint = contentColor)
            } else {
                Text(symbol, fontSize = if (symbol.length > 2) 20.sp else 28.sp, color = contentColor, fontWeight = FontWeight.Medium)
            }
        }
    }
}

private fun handleButtonClick(symbol: String, onAction: (CalculatorAction) -> Unit) {
    when (symbol) {
        "AC" -> onAction(CalculatorAction.Clear)
        "DEL" -> onAction(CalculatorAction.Delete)
        "." -> onAction(CalculatorAction.Decimal)
        "=" -> onAction(CalculatorAction.Calculate)
        "+" -> onAction(CalculatorAction.Operation(CalculatorOperation.Add))
        "-" -> onAction(CalculatorAction.Operation(CalculatorOperation.Subtract))
        "×" -> onAction(CalculatorAction.Operation(CalculatorOperation.Multiply))
        "÷" -> onAction(CalculatorAction.Operation(CalculatorOperation.Divide))
        "%" -> onAction(CalculatorAction.Operation(CalculatorOperation.Percent))
        "xʸ" -> onAction(CalculatorAction.Operation(CalculatorOperation.Power))
        "sin" -> onAction(CalculatorAction.Scientific(ScientificOperation.Sin))
        "cos" -> onAction(CalculatorAction.Scientific(ScientificOperation.Cos))
        "tan" -> onAction(CalculatorAction.Scientific(ScientificOperation.Tan))
        "ln" -> onAction(CalculatorAction.Scientific(ScientificOperation.Ln))
        "log" -> onAction(CalculatorAction.Scientific(ScientificOperation.Log))
        "√" -> onAction(CalculatorAction.Scientific(ScientificOperation.Sqrt))
        "1/x" -> onAction(CalculatorAction.Scientific(ScientificOperation.Reciprocal))
        "e" -> onAction(CalculatorAction.Scientific(ScientificOperation.E))
        "π" -> onAction(CalculatorAction.Scientific(ScientificOperation.Pi))
        else -> symbol.toIntOrNull()?.let { onAction(CalculatorAction.Number(it)) }
    }
}

// -----------------------------------------------------------------------------
// --- Previews ---
// -----------------------------------------------------------------------------

@Preview(showBackground = true, name = "Dark Mode Scientific Portrait")
@Composable
fun CalculatorScreenPreview() {
    ICalcTheme(darkTheme = true) {
        PortraitCalculatorLayout(state = CalculatorState(mode = CalculatorMode.Scientific), onAction = {}, navController = rememberNavController())
    }
}

@Preview(showBackground = true, name = "Dark Mode Landscape", widthDp = 800, heightDp = 360)
@Composable
fun CalculatorScreenPreviewLandscape() {
    ICalcTheme(darkTheme = true) {
        LandscapeCalculatorLayout(state = CalculatorState(mode = CalculatorMode.Scientific), onAction = {})
    }
}