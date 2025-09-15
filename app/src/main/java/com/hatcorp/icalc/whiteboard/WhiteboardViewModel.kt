package com.hatcorp.icalc.whiteboard

import androidx.compose.ui.graphics.Path
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

    fun clearCanvas() {
        _state.update {
            it.copy(paths = emptyList())
        }
    }
}