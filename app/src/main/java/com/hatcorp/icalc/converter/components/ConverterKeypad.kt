package com.hatcorp.icalc.converter.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hatcorp.icalc.R

@Composable
fun ConverterKeypad(
    modifier: Modifier = Modifier,
    onKeyPress: (String) -> Unit
) {
    val keys = listOf(
        listOf("7", "8", "9"),
        listOf("4", "5", "6"),
        listOf("1", "2", "3"),
        listOf("C", "0", ".")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Add a dedicated Delete button beside the main pad
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()){
            KeypadButton(symbol = "DEL", modifier = Modifier.weight(1f).height(64.dp), onClick = { onKeyPress("DEL") })
            Spacer(modifier = Modifier.weight(3.1f)) // Create space
        }

        keys.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { key ->
                    KeypadButton(symbol = key, modifier = Modifier.weight(1f), onClick = { onKeyPress(key) })
                }
            }
        }
    }
}

@Composable
fun KeypadButton(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.heightIn(min = 64.dp), // Use heightIn for flexibility
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (symbol == "DEL") {
                Icon(
                    painter = painterResource(id = R.drawable.ic_backspace),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(text = symbol, fontSize = 28.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}