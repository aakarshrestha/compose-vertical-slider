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
 * Creates a [ComposeVerticalSliderState] that is remembered across compositions.
 */
@Composable
fun rememberComposeVerticalSliderState(): ComposeVerticalSliderState {
    return remember {
        ComposeVerticalSliderState()
    }
}

/**
 * [ComposeVerticalSliderState] maintains the state of [ComposeVerticalSlider].
 */
class ComposeVerticalSliderState {

    var isEnabled = mutableStateOf(true)
        internal set

    var adjustTop = mutableStateOf(0f)
        internal set

    var progressValue = mutableStateOf(0)
        internal set

    private fun updateAdjustTopValue(value: Float) {
        this.adjustTop.value = value
    }

    private fun updateProgressValue(value: Int) {
        this.progressValue.value = value
    }


    /**
     * This method is executed when [MotionEvent] is [MotionEvent.ACTION_MOVE] and [MotionEvent.ACTION_UP]
     */
    internal fun updateOnTouch(
        motionEvent: MotionEvent,
        canvasHeight: Int
    ) {

        if (!isEnabled.value) return

        val y = motionEvent.y.roundToInt()

        updateAdjustTopValue(y.toFloat())

        val progress = calculateProgress(y.toFloat(), canvasHeight).coerceIn(MIN_VALUE, MAX_VALUE)
        updateProgressValue(progress)

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
}

/**
 * [ComposeVerticalSlider] allows users to make selections from the range of values
 * by dragging the slider in vertical axis.
 *
 * The min value that is allowed to choose is 0 and max value is 100.
 *
 * @param state maintains the state of ComposeVerticalSlider.
 * @param progressValue current value of the Slider. It can be null or the value can be assigned to it.
 * @param enabled whether or not component is enabled and can we interacted with or not.
 * @param trackColor that can be set to a desired color.
 * @param progressTrackColor that can be set to a desired color.
 * @param onProgressChanged lambda that is invoked when the slider value changes when [MotionEvent.ACTION_MOVE] is triggered.
 * @param onStopTrackingTouch lambda that is invoked when the slider value changes when [MotionEvent.ACTION_UP] is triggered.
 */

@Composable
fun ComposeVerticalSlider(
    state: ComposeVerticalSliderState,
    progressValueSet: Int? = null,
    enabled: Boolean = true,
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

    val enabledState by rememberSaveable { state.isEnabled }
    var adjustTop by rememberSaveable { state.adjustTop }
    var progressValue by rememberSaveable {

        if (progressValueSet != null) {
            state.progressValue.value = progressValueSet
            onProgressChanged(state.progressValue.value)
            onStopTrackingTouch(state.progressValue.value)
        }

        state.progressValue

    }

    val rect = Rect(left, top, right, bottom)
    val trackPaint = Paint().apply {
        color = trackColor
        isAntiAlias = true
        strokeWidth = 10f
    }
    val progressPaint = Paint().apply {
        color = if (enabledState) progressTrackColor else Color.Gray
        isAntiAlias = true
        strokeWidth = 10f
    }
    val path = Path()

    LaunchedEffect(enabledState) {
        state.isEnabled.value = enabled
    }

    Canvas(
        modifier = Modifier
            .pointerInteropFilter { motionEvent ->
                when(motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        enabledState
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (enabledState) {
                            state.updateOnTouch(motionEvent, canvasHeight)
                            adjustTop = state.adjustTop.value
                            progressValue = state.progressValue.value
                            onProgressChanged(progressValue)
                        }

                        enabledState
                    }
                    MotionEvent.ACTION_UP -> {
                        if (enabledState) {
                            state.updateOnTouch(motionEvent, canvasHeight)
                            adjustTop = state.adjustTop.value
                            progressValue = state.progressValue.value
                            onStopTrackingTouch(progressValue)
                        }

                        enabledState
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

        path.addRoundRect(roundRect = RoundRect(left, top, right, bottom, CornerRadius(x = radiusX, y = radiusY)) )
        aCanvas.clipPath(path = path, ClipOp.Intersect)

        aCanvas.drawRect(rect, trackPaint)

        if (rect.width > MIN_VALUE && rect.height > MIN_VALUE) {
            adjustTop = state.calculateAdjustTopFromProgressValue(progressValue, canvasHeight)
            aCanvas.drawRect(left, adjustTop, right, bottom, progressPaint)
        }
    }
}