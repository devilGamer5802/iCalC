package com.hatcorp.icalc.whiteboard

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.IntSize // Added import for IntSize
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WhiteboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(WhiteboardState())
    val state = _state.asStateFlow()

    fun addPath(path: Path) {
        _state.update {
            it.copy(paths = it.paths + path)
        }
    }

    fun undoLastPath() {
        _state.update {
            if (it.paths.isNotEmpty()) {
                it.copy(paths = it.paths.dropLast(1))
            } else {
                it
            }
        }
    }

    fun clearCanvas() {
        _state.update {
            it.copy(paths = emptyList())
        }
    }

    // Added setCanvasSize function
    fun setCanvasSize(size: IntSize){
        _state.update { it.copy(canvasSize = size) }
    }
}