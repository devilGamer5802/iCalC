package com.hatcorp.icalc.whiteboard

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.IntSize // Added import

// A simple state for now, just a list of paths the user has drawn.
data class WhiteboardState(
    val paths: List<Path> = emptyList(),
    val canvasSize: IntSize = IntSize.Zero // Added canvasSize property
)