package com.hatcorp.icalc.converter.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hatcorp.icalc.converter.UnitInfo

@Composable
fun ConversionRow(
    unit: UnitInfo,
    value: String,
    isActive: Boolean, // To show highlight or focus
    onActive: () -> Unit,
    isMenuExpanded: Boolean,
    onMenuToggle: () -> Unit,
    onUnitSelected: (UnitInfo) -> Unit,
    availableUnits: List<UnitInfo>,
    modifier: Modifier = Modifier
) {
    // Determine color based on active state
    val backgroundColor = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onActive)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f).clickable(onClick = onMenuToggle)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = unit.name, fontSize = 20.sp)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Open Menu")
                }
                Text(text = unit.symbol, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = value, fontSize = 32.sp)
        }
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = onMenuToggle
        ) {
            availableUnits.forEach { unitInfo ->
                DropdownMenuItem(
                    text = { Text("${unitInfo.name} (${unitInfo.symbol})") },
                    onClick = { onUnitSelected(unitInfo) }
                )
            }
        }
    }
}