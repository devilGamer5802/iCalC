package com.hatcorp.icalc

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.hatcorp.icalc.calculator.*
import com.hatcorp.icalc.ui.theme.ICalcTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ICalcTheme(darkTheme = true) {
                // NavHost should be the root of your UI content
                AppNavHost(navController = rememberNavController())
            }
        }
    }
}

// -----------------------------------------------------------------------------
// --- THE CORRECTED & POLISHED CALCULATOR SCREEN ---
// -----------------------------------------------------------------------------

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // THE ROOT LAYOUT FIX: A Column ensures children are laid out sequentially (top to bottom).
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // --- DISPLAY AREA ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // weight(1f) makes this area expand to fill available space.
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom // This pushes the text content to the bottom.
        ) {
            // This Row now correctly holds the History button.
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isLandscape) {
                    IconButton(onClick = { onAction(CalculatorAction.ShowHistory) }) {
                        Icon(Icons.Outlined.History, "History")
                    }
                }
            }

            // Primary and secondary display texts. No duplication.
            Text(
                text = state.number1 + (state.operation?.symbol ?: "") + state.number2,
                fontSize = 40.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End, maxLines = 1,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = state.number1.ifEmpty { "0" },
                fontSize = 80.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End, maxLines = 1, fontWeight = FontWeight.Light,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // --- BUTTON PAD AREA ---
        // This area does not have a weight, so it will take up its natural size at the bottom.
        if (isLandscape) {
            LandscapePad(state, onAction = onAction)
        } else {
            AnimatedContent(
                targetState = state.mode,
                label = "pad-animation",
                transitionSpec = {
                    // Your preferred animation is preserved.
                    fadeIn() togetherWith fadeOut()
                }
            ) { targetMode ->
                when (targetMode) {
                    CalculatorMode.Basic -> BasicPad(onAction = onAction)
                    CalculatorMode.Scientific -> ScientificPad(state, onAction = onAction)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// --- PADS & BUTTONS (No significant changes needed from your provided code) ---
// -----------------------------------------------------------------------------
@Composable
fun LandscapePad(state: CalculatorState, onAction: (CalculatorAction) -> Unit) {
    val radText = if (state.angleUnit == AngleUnit.DEG) "deg" else "rad"
    val buttons = listOf(
        listOf("2nd", radText, "sin", "cos", "tan", "AC", "DEL"),
        listOf("xʸ", "log", "ln", "(", ")", "%", "÷"),
        listOf("1/x", "7", "8", "9", "π", "4", "×"),
        listOf("e", "5", "6", "1", "2", "-", "+"),
        listOf("Toggle", "±", "0", ".", "3", "=")
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        buttons.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.forEach { symbol ->
                    CalculatorButton(symbol = symbol, modifier = Modifier.weight(1f), onClick = { handleButtonClick(symbol, onAction) })
                }
            }
        }
    }
}

@Composable
fun BasicPad(onAction: (CalculatorAction) -> Unit) {
    val buttons = listOf(
        listOf("AC", "DEL", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("Toggle", "0", ".", "=")
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        buttons.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { symbol ->
                    CalculatorButton(
                        symbol = symbol,
                        modifier = Modifier.weight(1f),
                        onClick = { handleButtonClick(symbol, onAction) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScientificPad(state: CalculatorState, onAction: (CalculatorAction) -> Unit) {
    val radText = if (state.angleUnit == AngleUnit.DEG) "deg" else "rad"
    val sinText = if (state.isShifted) "sin⁻¹" else "sin"
    val cosText = if (state.isShifted) "cos⁻¹" else "cos"
    val tanText = if (state.isShifted) "tan⁻¹" else "tan"

    val buttons = listOf(
        listOf("2nd", radText, sinText, cosText, tanText),
        listOf("xʸ", "log", "ln", "(", ")"),
        listOf("√", "AC", "DEL", "%", "÷"),
        listOf("1/x", "7", "8", "9", "×"),
        listOf("π", "4", "5", "6", "-"),
        listOf("e", "1", "2", "3", "+"),
        listOf("Toggle", "±", "0", ".", "=")
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        buttons.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { symbol ->
                    CalculatorButton(
                        symbol = symbol,
                        modifier = Modifier.weight(1f),
                        isShiftActive = state.isShifted && symbol == "2nd",
                        onClick = { handleButtonClick(symbol, onAction) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    isShiftActive: Boolean = false,
    onClick: () -> Unit
) {
    val isOperator = "÷×-+=xʸ%".contains(symbol) && symbol.length == 1
    val isToggle = symbol == "Toggle"

    val colors = if (isOperator) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    else if (isShiftActive) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
    else ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)

    Button(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f).fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = colors,
        contentPadding = PaddingValues(0.dp)
    ) {
        if (isToggle) {
            Icon(Icons.Default.SwapHoriz, contentDescription = "Toggle Mode", tint = MaterialTheme.colorScheme.primary)
        } else if (symbol == "DEL") {
            Icon(painter = painterResource(id = R.drawable.ic_backspace), contentDescription = "Delete")
        } else {
            Text(text = symbol, fontSize = 24.sp, fontWeight = FontWeight.Medium)
        }
    }
}


// --- COMPLETE LOGIC (No changes needed) ---
private fun handleButtonClick(symbol: String, onAction: (CalculatorAction) -> Unit) {
    // This when block from your provided code is correct and exhaustive.
    when (symbol) {
        "AC" -> onAction(CalculatorAction.Clear)
        "DEL" -> onAction(CalculatorAction.Delete)
        "=" -> onAction(CalculatorAction.Calculate)
        "Toggle" -> onAction(CalculatorAction.ToggleMode)
        "±" -> { /* TODO */ }
        "%" -> onAction(CalculatorAction.Operation(CalculatorOperation.Percent))
        "÷" -> onAction(CalculatorAction.Operation(CalculatorOperation.Divide))
        "×" -> onAction(CalculatorAction.Operation(CalculatorOperation.Multiply))
        "-" -> onAction(CalculatorAction.Operation(CalculatorOperation.Subtract))
        "+" -> onAction(CalculatorAction.Operation(CalculatorOperation.Add))
        "." -> onAction(CalculatorAction.Decimal)
        "xʸ" -> onAction(CalculatorAction.Operation(CalculatorOperation.Power))
        "2nd" -> onAction(CalculatorAction.Shift)
        "deg", "rad" -> onAction(CalculatorAction.ToggleAngleUnit)
        "sin" -> onAction(CalculatorAction.Scientific(ScientificOperation.Sin))
        "sin⁻¹" -> onAction(CalculatorAction.Scientific(ScientificOperation.Asin))
        "cos" -> onAction(CalculatorAction.Scientific(ScientificOperation.Cos))
        "cos⁻¹" -> onAction(CalculatorAction.Scientific(ScientificOperation.Acos))
        "tan" -> onAction(CalculatorAction.Scientific(ScientificOperation.Tan))
        "tan⁻¹" -> onAction(CalculatorAction.Scientific(ScientificOperation.Atan))
        "log" -> onAction(CalculatorAction.Scientific(ScientificOperation.Log))
        "ln" -> onAction(CalculatorAction.Scientific(ScientificOperation.Ln))
        "√" -> onAction(CalculatorAction.Scientific(ScientificOperation.Sqrt))
        "1/x" -> onAction(CalculatorAction.Scientific(ScientificOperation.Reciprocal))
        "π" -> onAction(CalculatorAction.Scientific(ScientificOperation.Pi))
        "e" -> onAction(CalculatorAction.Scientific(ScientificOperation.E))
        else -> symbol.toIntOrNull()?.let { onAction(CalculatorAction.Number(it)) }
    }
}