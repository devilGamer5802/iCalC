package com.hatcorp.icalc.whiteboard

import androidx.compose.ui.graphics.Path

// A simple state for now, just a list of paths the user has drawn.
data class WhiteboardState(
    val paths: List<Path> = emptyList()
)