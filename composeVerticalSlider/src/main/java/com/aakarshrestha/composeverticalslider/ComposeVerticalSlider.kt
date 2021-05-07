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

/**
 * [ComposeVerticalSlider] allows users to make selections from the range of values
 * by dragging the slider in vertical axis.
 *
 * The min value that is allowed to choose is 0 and max value is 100.
 *
 * @param trackColor that can be set to a desired color.
 * @param progressTrackColor that can be set to a desired color.
 * @param onProgressChanged lambda that is invoked when the slider value changes when [MotionEvent.ACTION_MOVE] is triggered.
 * @param onStopTrackingTouch lambda that is invoked when the slider value changes when [MotionEvent.ACTION_UP] is triggered.
 */

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
    var progressValue by rememberSaveable { mutableStateOf(0) }

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
                                progressValue = it
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
                                progressValue = it
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
            adjustTop = calculateAdjustTopFromProgressValue(progressValue, canvasHeight)
            aCanvas.drawRect(left, adjustTop, right, bottom, progressPaint)
        }
    }

}

/**
 * This method is executed when [MotionEvent] is [MotionEvent.ACTION_MOVE] and [MotionEvent.ACTION_UP]
 * @param adjustTopValue lambda that provides the changed value to adjust the slider in vertical axis.
 * @param progressValue lambda that provides the changed value to adjust the progress that is in the range of 0 to 100.
 */
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

/**
 * Outputs the progress value when slider is updated in vertical axis.
 */
private fun calculateProgress(adjustTop: Float, canvasHeight: Int): Int {
    return MAX_VALUE - (adjustTop / canvasHeight).times(100).roundToInt()
}

/**
 * Calculate the y axis value based on progress value.
 */
fun calculateAdjustTopFromProgressValue(progressValue: Int, canvasHeight: Int): Float {
    return (MAX_VALUE - progressValue).times(canvasHeight).div(100).toFloat()
}