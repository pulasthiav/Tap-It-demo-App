package com.pulasthi.tapit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun DashboardWavyLines(
    modifier: Modifier = Modifier,
    lineColor: Color = Color.White.copy(alpha = 0.1f),
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2f
        val waveHeight = size.height * 0.35f

        fun wavePath(baseY: Float, amplitude: Float): Path {
            return Path().apply {
                moveTo(0f, baseY)
                cubicTo(
                    size.width * 0.25f, baseY - amplitude,
                    size.width * 0.55f, baseY + amplitude,
                    size.width, baseY,
                )
            }
        }

        drawPath(
            path = wavePath(waveHeight, size.height * 0.12f),
            color = lineColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )
        drawPath(
            path = wavePath(waveHeight * 1.4f, size.height * 0.08f),
            color = lineColor.copy(alpha = lineColor.alpha * 0.7f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )
        drawLine(
            color = lineColor.copy(alpha = lineColor.alpha * 0.5f),
            start = Offset(size.width * 0.1f, waveHeight * 0.6f),
            end = Offset(size.width * 0.9f, waveHeight * 0.6f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}
