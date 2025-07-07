package me.nikhilchaudhari.audiopluginui.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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