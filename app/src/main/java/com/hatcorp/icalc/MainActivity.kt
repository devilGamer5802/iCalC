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
            // ... (History Bottom Sheet UI from Chapter 9.7) ...
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
        // --- DISPLAY AREA ---
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onAction(CalculatorAction.ToggleMode) }) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Switch Mode",
                        tint = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Row {
                    IconButton(onClick = { onAction(CalculatorAction.ShowHistory) }) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "History",
                            tint = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    IconButton(onClick = { navController.navigate(AppRoutes.CONVERTER_LIST) }) {
                        Icon(
                            imageVector = Icons.Outlined.Widgets,
                            contentDescription = "Utilities",
                            tint = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = state.number1 + (state.operation?.symbol ?: "") + state.number2,
                    fontSize = 40.sp,
                    color = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary,
                    textAlign = TextAlign.End, maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.number1.ifEmpty { "0" },
                    fontSize = 80.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.End, maxLines = 1, fontWeight = FontWeight.Light
                )
            }
        }

        // --- BUTTON PAD ---
        ButtonPad(state = state, onAction = onAction)
    }
}

@Composable
fun LandscapeCalculatorLayout(state: CalculatorState, onAction: (CalculatorAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = state.number1 + (state.operation?.symbol ?: "") + state.number2,
                fontSize = 24.sp,
                color = if (isSystemInDarkTheme()) DarkTextSecondary else LightTextSecondary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.number1.ifEmpty { "0" },
                fontSize = 48.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Light, maxLines = 1
            )
        }

        Box(modifier = Modifier.weight(1.5f).fillMaxHeight()) {
            ButtonPad(state = state, onAction = onAction)
        }
    }
}

// -----------------------------------------------------------------------------
// --- Reusable UI Components & Logic ---
// -----------------------------------------------------------------------------

@Composable
fun ButtonPad(state: CalculatorState, onAction: (CalculatorAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    CalculatorButton(symbol = "2nd", modifier = Modifier.weight(1f), isShiftActive = state.isShifted, onClick = { onAction(CalculatorAction.Shift) })
                    CalculatorButton(symbol = sqrtText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(sqrtText, onAction) })
                    CalculatorButton(symbol = squareText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(squareText, onAction) })
                    CalculatorButton(symbol = piText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(piText, onAction) })
                    CalculatorButton(symbol = "x!", modifier = Modifier.weight(1f), onClick = { handleButtonClick("x!", onAction) })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CalculatorButton(symbol = sinText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(sinText, onAction) })
                    CalculatorButton(symbol = cosText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(cosText, onAction) })
                    CalculatorButton(symbol = tanText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(tanText, onAction) })
                    CalculatorButton(symbol = lnText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(lnText, onAction) })
                    CalculatorButton(symbol = logText, modifier = Modifier.weight(1f), onClick = { handleButtonClick(logText, onAction) })
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
                    val weight = if (button == "0") 2.05f else 1f
                    val aspectRatio = if (button == "0") 2f else 1f
                    CalculatorButton(symbol = button, modifier = Modifier.weight(weight), aspectRatio = aspectRatio, onClick = { handleButtonClick(button, onAction) })
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 1f,
    isShiftActive: Boolean = false,
    onClick: () -> Unit = {}
) {
    val isOperator = "÷×-+=".contains(symbol)
    val isSpecialFunction = "ACDEL%".contains(symbol)

    val buttonColor = when {
        isShiftActive -> MaterialTheme.colorScheme.tertiaryContainer
        isOperator -> MaterialTheme.colorScheme.primary
        isSpecialFunction -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.secondary
    }
    val contentColor = when {
        isShiftActive -> MaterialTheme.colorScheme.onTertiaryContainer
        isOperator -> MaterialTheme.colorScheme.onPrimary
        isSpecialFunction -> MaterialTheme.colorScheme.onBackground
        else -> MaterialTheme.colorScheme.onSecondary
    }

    Surface(
        modifier = modifier.aspectRatio(aspectRatio).fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = buttonColor,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (symbol == "DEL") {
                Icon(painter = painterResource(id = R.drawable.ic_backspace), contentDescription = "Delete", tint = contentColor)
            } else {
                Text(text = symbol, fontSize = 28.sp, color = contentColor, fontWeight = FontWeight.Medium)
            }
        }
    }
}

private fun handleButtonClick(symbol: String, onAction: (CalculatorAction) -> Unit) {
    when (symbol) {
        // Basic Actions
        "AC" -> onAction(CalculatorAction.Clear)
        "DEL" -> onAction(CalculatorAction.Delete)
        "%" -> onAction(CalculatorAction.Operation(CalculatorOperation.Percent))
        "÷" -> onAction(CalculatorAction.Operation(CalculatorOperation.Divide))
        "×" -> onAction(CalculatorAction.Operation(CalculatorOperation.Multiply))
        "-" -> onAction(CalculatorAction.Operation(CalculatorOperation.Subtract))
        "+" -> onAction(CalculatorAction.Operation(CalculatorOperation.Add))
        "." -> onAction(CalculatorAction.Decimal)
        "=" -> onAction(CalculatorAction.Calculate)

        // Scientific Actions - Non-Shifted
        "sin" -> onAction(CalculatorAction.Scientific(ScientificOperation.Sin))
        "cos" -> onAction(CalculatorAction.Scientific(ScientificOperation.Cos))
        "tan" -> onAction(CalculatorAction.Scientific(ScientificOperation.Tan))
        "ln" -> onAction(CalculatorAction.Scientific(ScientificOperation.Ln))
        "log" -> onAction(CalculatorAction.Scientific(ScientificOperation.Log))
        "√" -> onAction(CalculatorAction.Scientific(ScientificOperation.Sqrt))
        "x²" -> onAction(CalculatorAction.Scientific(ScientificOperation.Square))
        "π" -> onAction(CalculatorAction.Scientific(ScientificOperation.Pi))
        "x!" -> onAction(CalculatorAction.Scientific(ScientificOperation.Factorial))
        "e" -> onAction(CalculatorAction.Scientific(ScientificOperation.E))

        // Scientific Actions - Shifted
        "sin⁻¹" -> onAction(CalculatorAction.Scientific(ScientificOperation.Asin))
        "cos⁻¹" -> onAction(CalculatorAction.Scientific(ScientificOperation.Acos))
        "tan⁻¹" -> onAction(CalculatorAction.Scientific(ScientificOperation.Atan))
        "eˣ" -> onAction(CalculatorAction.Scientific(ScientificOperation.EPower))
        "10ˣ" -> onAction(CalculatorAction.Scientific(ScientificOperation.TenPower))
        "∛" -> onAction(CalculatorAction.Scientific(ScientificOperation.Cbrt))
        "x³" -> onAction(CalculatorAction.Scientific(ScientificOperation.Cube))

        // Number actions
        else -> symbol.toIntOrNull()?.let { onAction(CalculatorAction.Number(it)) }
    }
}


// -----------------------------------------------------------------------------
// --- Previews ---
// -----------------------------------------------------------------------------

@Preview(showBackground = true, name = "Light Mode Portrait")
@Composable
fun CalculatorScreenPreviewLight() {
    ICalcTheme(darkTheme = false) {
        val state = CalculatorState(number1 = "12,345", mode = CalculatorMode.Scientific, number2 = "*2")
        PortraitCalculatorLayout(state = state, onAction = {}, navController = rememberNavController())
    }
}

@Preview(showBackground = true, name = "Dark Mode Portrait")
@Composable
fun CalculatorScreenPreviewDark() {
    ICalcTheme(darkTheme = true) {
        val state = CalculatorState(number1 = "987.6", mode = CalculatorMode.Scientific, isShifted = true)
        PortraitCalculatorLayout(state = state, onAction = {}, navController = rememberNavController())
    }
}

@Preview(showBackground = true, name = "Dark Mode Landscape", widthDp = 800, heightDp = 360)
@Composable
fun CalculatorScreenPreviewLandscape() {
    ICalcTheme(darkTheme = true) {
        val state = CalculatorState(number1 = "1.23E4", mode = CalculatorMode.Scientific)
        LandscapeCalculatorLayout(state = state, onAction = {})
    }
}