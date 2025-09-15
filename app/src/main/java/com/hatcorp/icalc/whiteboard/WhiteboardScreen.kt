package com.hatcorp.icalc.whiteboard

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.graphics.createBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhiteboardScreen() {
    val viewModel = viewModel<WhiteboardViewModel>()
    // We don't need to observe the whole state here, as we are handling paths locally for performance

    // This path is the one currently being drawn.
    var currentPath by remember { mutableStateOf(Path()) }
    // This stores the last position of the pointer for calculating curves.
    var lastPosition by remember { mutableStateOf<Offset?>(null) }
    // This bitmap is our "background" layer where finished paths are saved.
    var committedBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val pathColor = MaterialTheme.colorScheme.primary
    val pathStroke = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)

    // This will be used to draw onto our bitmap.
    val canvas = remember(committedBitmap) {
        committedBitmap?.let { Canvas(it) }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = {
                    // Clear both the state and the local bitmap
                    viewModel.clearCanvas()
                    committedBitmap = createBitmap(canvasSize.width, canvasSize.height).asImageBitmap()
                }) {
                    Icon(Icons.Outlined.Clear, contentDescription = "Clear Canvas")
                }
                Spacer(modifier = Modifier.weight(1f))
                FloatingActionButton(onClick = { /* TODO: Send to Gemini */ }) {
                    Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = "Solve")
                }
            }
        }
    ) { padding ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .onSizeChanged { size ->
                    // Initialize our bitmap canvas when the main Canvas's size is known.
                    if (canvasSize != size) {
                        canvasSize = size
                        committedBitmap = createBitmap(size.width, size.height).asImageBitmap()
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            // Start a new path at the exact touch point
                            currentPath = Path().apply { moveTo(offset.x, offset.y) }
                            lastPosition = offset
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val currentPosition = change.position
                            lastPosition?.let {
                                // THE SMOOTHING LOGIC: Use a quadratic Bezier curve
                                // from the last point towards the new point.
                                currentPath.quadraticTo(
                                    it.x, it.y,
                                    (it.x + currentPosition.x) / 2,
                                    (it.y + currentPosition.y) / 2
                                )
                            }
                            lastPosition = currentPosition
                        },
                        onDragEnd = {
                            // Drag has ended. Draw the "current" path onto our permanent bitmap.
                            canvas?.drawPath(currentPath, Paint().apply {
                                this.color = pathColor
                                this.style = PaintingStyle.Stroke
                                this.strokeWidth = 8f
                                this.strokeCap = StrokeCap.Round
                                this.strokeJoin = StrokeJoin.Round
                            })
                            // Reset the live path for the next drawing.
                            currentPath = Path()
                            lastPosition = null
                        }
                    )
                }
        ) {
            // THE PERFORMANCE OPTIMIZATION:
            // 1. Draw the bitmap containing all previous paths. This is a very fast operation.
            committedBitmap?.let {
                drawImage(it)
            }
            // 2. Draw only the path that is currently being drawn on top.
            // This ensures real-time feedback without redrawing everything.
            drawPath(
                path = currentPath,
                color = pathColor,
                style = pathStroke
            )
        }
    }
}