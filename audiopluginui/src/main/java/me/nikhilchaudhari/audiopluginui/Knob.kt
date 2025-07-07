package me.nikhilchaudhari.audiopluginui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.nikhilchaudhari.audiopluginui.commons.ValueTextBox
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


/**
 * This is a parameter knob that can be used to control a parameter.
 * It is a simple knob that can be dragged to change the value.
 * The design is more like a classic knob with ticks around and a value text box at the bottom.
 * It has a marker/indicator at the center of the knob that points where the knob is at.
 * For usage please check [ParameterKnobPreview]
 *
 *
 * @param value The initial value of the knob. No need to pass as state value.
 * @param onValueChange The callback that is called when the value is changed.
 * @param modifier The [Modifier] to be applied to the knob.
 * @param minValue The minimum value of the knob.
 * @param maxValue The maximum value of the knob.
 * @param steps The number of steps to use for the knob.
 * @param knobColor The [Color] of the knob.
 * @param markerColor The [Color] of the marker.
 * @param ticksColor The [Color] of the ticks.
 */
@Composable
fun ParameterKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Float = 0f,
    maxValue: Float = 1f,
    steps: Int = 100,
    knobColor: Color = Color.Blue,
    markerColor: Color = Color.Red,
    ticksColor: Color = Color.Black,
) {

    var knobValue by remember { mutableFloatStateOf(value) }
    var showValueTextBox by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Canvas(
            modifier = modifier.then(
                Modifier
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                showValueTextBox = true
                            },
                            onDrag = { change, dragAmount ->
                                val combinedDrag = dragAmount.x + dragAmount.y
                                val rawValue =
                                    (knobValue + combinedDrag / 1000f).coerceIn(minValue, maxValue)
                                val steppedValue = if (steps > 0) {
                                    val stepSize = (maxValue - minValue) / steps
                                    ((rawValue - minValue) / stepSize).roundToInt() * stepSize + minValue
                                } else {
                                    rawValue
                                }
                                knobValue = (steppedValue * 100f).roundToInt() / 100f
                                onValueChange(steppedValue)
                            },
                            onDragEnd = {
                                showValueTextBox = false
                            }
                        )
                    }
                    .padding(start = 12.dp, end = 12.dp, top = 12.dp)
            )
        ) {

            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.width / 2
            val shadowOffset = 1.dp.toPx()

            val normalizedValue = (knobValue - minValue) / (maxValue - minValue)
            val angle = 2 * Math.PI / 3 + normalizedValue * ((2 * Math.PI) - Math.PI / 3)
            val startX = centerX + (radius * 0.8f * cos(angle)).toFloat()
            val startY = centerY + (radius * 0.8f * sin(angle)).toFloat()

            drawCircle(
                color = Color.Black.copy(alpha = 0.2f),
                radius = radius,
                center = Offset(centerX + shadowOffset, centerY + shadowOffset),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            drawCircle(
                color = knobColor,
                radius = radius,
                center = Offset(centerX, centerY)
            )

            drawLine(
                color = markerColor,
                start = Offset(startX, startY),
                end = Offset(centerX, centerY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )

            val measurementCount = 21
            val startAngle = 2 * Math.PI / 3
            val endAngle = startAngle + ((2 * Math.PI) - Math.PI / 3)
            val angleStep = (endAngle - startAngle) / (measurementCount - 1)

            for (i in 0 until measurementCount) {
                val measurementAngle = startAngle + (i * angleStep)

                val innerRadius = radius + 4.dp.toPx()
                val outerRadius = radius + 8.dp.toPx()

                val innerX = centerX + (innerRadius * cos(measurementAngle)).toFloat()
                val innerY = centerY + (innerRadius * sin(measurementAngle)).toFloat()
                val outerX = centerX + (outerRadius * cos(measurementAngle)).toFloat()
                val outerY = centerY + (outerRadius * sin(measurementAngle)).toFloat()

                drawLine(
                    color = ticksColor,
                    start = Offset(innerX, innerY),
                    end = Offset(outerX, outerY),
                    strokeWidth = 1.dp.toPx(),
                    cap = StrokeCap.Square
                )
            }

        }
        AnimatedVisibility(
            visible = showValueTextBox,
            enter = fadeIn(animationSpec = tween(easing = LinearEasing)),
            exit = fadeOut(animationSpec = tween(easing = LinearEasing))
        ) {
            ValueTextBox(value = knobValue)
        }
    }
}


@Preview
@Composable
private fun ParameterKnobPreview() {
    ParameterKnob(
        value = 0.0f,
        onValueChange = {},
        modifier = Modifier.size(100.dp),
        minValue = 0f,
        maxValue = 1f,
        steps = 100,
        knobColor = Color.Magenta,
        markerColor = Color.White
    )
}


/**
 * This is a gauge knob that can be used to control a parameter.
 * It is a simple knob that can be dragged to change the value.
 * The design is sort of level indicator that fills the knob as the knob is dragged.
 * Please check [GaugeKnobPreview] for usage.
 *
 * @param value The initial value of the knob. No need to pass as state value.
 * @param onValueChange The callback that is called when the value is changed.
 * @param modifier The [Modifier] to be applied to the knob.
 * @param minValue The minimum value of the knob.
 * @param maxValue The maximum value of the knob.
 * @param knobColors The [List] of [Color]s to use for the knob track/level as gradient.
 */
@Composable
fun GaugeKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Float = 0f,
    maxValue: Float = 1f,
    knobColors: List<Color> = listOf(Color.Red, Color.Yellow)
) {
    var knobValue by remember { mutableFloatStateOf(value) }
    Box(
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = modifier.then(
                Modifier
                    .padding(start = 12.dp, end = 12.dp, top = 12.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                val combinedDrag = dragAmount.x + dragAmount.y
                                knobValue = (knobValue + combinedDrag / 1000f).coerceIn(minValue, maxValue)
                                onValueChange(knobValue)
                            }
                        )
                    }
            )
        ) {
            val startAngle = 2 * Math.PI / 3
            val endAngle = startAngle + ((2 * Math.PI) - Math.PI / 3)
            val currentAngle = startAngle + (endAngle - startAngle) * knobValue

            drawPath(
                path = Path().apply {
                    val center = size.center
                    val radius = size.minDimension / 2 * 0.8f

                    moveTo(
                        x = center.x + radius * cos(startAngle.toFloat()),
                        y = center.y + radius * sin(startAngle.toFloat())
                    )

                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            left = center.x - radius,
                            top = center.y - radius,
                            right = center.x + radius,
                            bottom = center.y + radius
                        ),
                        startAngleDegrees = Math.toDegrees(startAngle).toFloat(),
                        sweepAngleDegrees = Math.toDegrees(currentAngle - startAngle).toFloat(),
                        forceMoveTo = false
                    )
                },
                brush = Brush.linearGradient(knobColors),
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@Preview
@Composable
fun GaugeKnobPreview() {
    GaugeKnob(
        value = 0.5f,
        onValueChange = {},
        modifier = Modifier.size(100.dp),
        minValue = 0f,
        maxValue = 1f,
        knobColors = listOf(Color.Red, Color.Blue)
    )
}