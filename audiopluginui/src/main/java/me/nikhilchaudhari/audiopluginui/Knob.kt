package me.nikhilchaudhari.audiopluginui

import android.R.attr.strokeWidth
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.rem


@Composable
fun Knob(
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
                Modifier.pointerInput(Unit) {
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


@Composable
fun ValueTextBox(
    value: Float,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    backgroundColor: Color = Color.Black
) {
    Box(
        modifier = Modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(4.dp))
            .border(0.5.dp, color = textColor, shape = RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString(),
            color = textColor,
            modifier = modifier.padding(4.dp)
        )
    }
}


@Preview
@Composable
private fun KnobPreview() {
    Knob(
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