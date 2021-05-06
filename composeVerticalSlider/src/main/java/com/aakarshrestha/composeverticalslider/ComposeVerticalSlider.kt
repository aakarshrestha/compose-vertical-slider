package com.aakarshrestha.composeverticalslider

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private const val MAX_VALUE = 100
private const val MIN_VALUE = 0

@Composable
fun ComposeVerticalSlider(
    trackColor: Color = Color.LightGray,
    progressTrackColor: Color = Color.Green,
    onProgressChanged: (Int) -> Unit,
    onStopTrackingTouch: (Int) -> Unit
) {

    val left = 0f
    val top = 0f
    var right by remember { mutableStateOf(0f) }
    var bottom by remember { mutableStateOf(0f) }

    var canvasHeight by remember { mutableStateOf(0) }

    val radiusX = 80f
    val radiusY = 80f

    var adjustTop by rememberSaveable { mutableStateOf(bottom) }

    val rect = Rect(left, top, right, bottom)
    val trackPaint = Paint().apply {
        color = trackColor
        isAntiAlias = true
        strokeWidth = 10f
    }

    val progressPaint = Paint().apply {
        color = progressTrackColor
        isAntiAlias = true
        strokeWidth = 10f
    }

    Canvas(
        modifier = Modifier
            .pointerInteropFilter { motionEvent ->
                when(motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> true
                    MotionEvent.ACTION_MOVE -> {

                        updateOnTouch(motionEvent, canvasHeight,
                            adjustTopValue = {
                                adjustTop = it
                            },
                            progressValue = {
                                onProgressChanged(it)
                            }
                        )

                        true
                    }
                    MotionEvent.ACTION_UP -> {

                        updateOnTouch(motionEvent, canvasHeight,
                            adjustTopValue = {
                                adjustTop = it
                            },
                            progressValue = {
                                onStopTrackingTouch(it)
                            }
                        )

                        true
                    }
                    else -> false
                }
            }
            .width(180.dp)
            .height(360.dp)
    ) {

        canvasHeight = size.height.roundToInt()

        right = size.width
        bottom = size.height

        val aCanvas = drawContext.canvas

        val path = Path()
        path.addRoundRect(roundRect = RoundRect(left, top, right, bottom, CornerRadius(x = radiusX, y = radiusY)) )
        aCanvas.clipPath(path = path, ClipOp.Intersect)

        aCanvas.drawRect(rect, trackPaint)

        if (rect.width > MIN_VALUE && rect.height > MIN_VALUE) {
            aCanvas.drawRect(left, adjustTop, right, bottom, progressPaint)
        }
    }

}

private fun updateOnTouch(
    motionEvent: MotionEvent,
    canvasHeight: Int,
    adjustTopValue: (Float) -> Unit,
    progressValue: (Int) -> Unit
) {

    val y = motionEvent.y.roundToInt()

    adjustTopValue(y.toFloat())
    val progress = calculateProgress(y.toFloat(), canvasHeight).coerceIn(MIN_VALUE, MAX_VALUE)

    progressValue(progress)
}

private fun calculateProgress(adjustTop: Float, canvasHeight: Int): Int {
    return MAX_VALUE - (adjustTop / canvasHeight).times(100).roundToInt()
}