package com.hatcorp.icalc.whiteboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send // Updated import
import androidx.compose.material.icons.automirrored.outlined.Undo // Updated import
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
// import androidx.compose.ui.graphics.drawscope.DrawScope // Unused import removed
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
// import androidx.compose.ui.unit.dp // Unused import removed
// import androidx.lifecycle.ViewModel // Unused import removed
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhiteboardScreen() {
    val viewModel = viewModel<WhiteboardViewModel>()
    val state by viewModel.state.collectAsState()

    val primaryColor = MaterialTheme.colorScheme.primary // Resolve color in Composable scope

    // Local state for the path currently being drawn by the user in real-time.
    var currentPath by remember { mutableStateOf<Path?>(Path()) }
    // Local state for the last pointer position, used for Bezier curve smoothing.
    var lastPosition by remember { mutableStateOf<Offset?>(null) }

    val committedBitmap by remember(state.paths, state.canvasSize, primaryColor) { // Added primaryColor to remember key if it affects bitmap drawing logic
        mutableStateOf(
            if (state.paths.isNotEmpty() && state.canvasSize.width > 0 && state.canvasSize.height > 0) {
                drawPathsToBitmap(state.paths, state.canvasSize, primaryColor) // Use resolved color
            } else {
                null
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = { viewModel.undoLastPath() }) {
                    Icon(Icons.AutoMirrored.Outlined.Undo, contentDescription = "Undo") // Updated icon
                }
                IconButton(onClick = { viewModel.clearCanvas() }) {
                    Icon(Icons.Outlined.ClearAll, contentDescription = "Clear Canvas")
                }
                Spacer(modifier = Modifier.weight(1f))
                FloatingActionButton(onClick = { /* TODO: Send to Gemini */ }) {
                    Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = "Solve") // Updated icon
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { size ->
                        viewModel.setCanvasSize(size)
                    }
                    .graphicsLayer(alpha = 0.99f)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPath = Path().apply { moveTo(offset.x, offset.y) }
                                lastPosition = offset
                            },
                            onDrag = { change, _ ->
                                val currentPosition = change.position
                                lastPosition?.let {
                                    currentPath?.quadraticTo( // Updated to quadraticTo
                                        it.x, it.y,
                                        (it.x + currentPosition.x) / 2,
                                        (it.y + currentPosition.y) / 2
                                    )
                                }
                                lastPosition = currentPosition
                                change.consume()
                            },
                            onDragEnd = {
                                currentPath?.let { viewModel.addPath(it) }
                                currentPath = Path()
                                lastPosition = null
                            }
                        )
                    }
            ) {
                committedBitmap?.let {
                    drawImage(it)
                }
                currentPath?.let {
                    drawPath(it, color = primaryColor, style = Stroke(8f, cap = StrokeCap.Round, join = StrokeJoin.Round)) // Use resolved color
                }
            }
        }
    }
}

private fun drawPathsToBitmap(paths: List<Path>, size: IntSize, color: Color): ImageBitmap {
    val bitmap = ImageBitmap(size.width, size.height, ImageBitmapConfig.Argb8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        this.color = color // Already accepts Color parameter
        this.style = PaintingStyle.Stroke
        this.strokeWidth = 8f
        this.strokeCap = StrokeCap.Round
        this.strokeJoin = StrokeJoin.Round
    }
    paths.forEach { path ->
        canvas.drawPath(path, paint)
    }
    return bitmap
}
