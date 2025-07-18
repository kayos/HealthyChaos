package com.kayos.healthykayos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kayos.device.HrData

@Composable
fun HeartRateGraph(
    samples: List<HrData.HrSample>,
    modifier: Modifier = Modifier
) {
    val maxBpm = samples.maxOfOrNull { it.bpm + 5 } ?: 0
    val minBpm = samples.minOfOrNull { it.bpm - 5 } ?: 0
    val maxTime = samples.maxOfOrNull { it.secondsFromStart } ?: 1L

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val width = size.width
            val height = size.height
            val axisPadding = 32f

            // Draw axes
            drawLine(
                color = Color.Companion.Gray,
                start = Offset(axisPadding, 0f),
                end = Offset(axisPadding, height - axisPadding),
                strokeWidth = 3f
            )
            drawLine(
                color = Color.Companion.Gray,
                start = Offset(axisPadding, height - axisPadding),
                end = Offset(width, height - axisPadding),
                strokeWidth = 3f
            )

            if (samples.size > 1) {
                val timeRange = maxTime.toFloat()
                val bpmRange = (maxBpm - minBpm).toFloat().coerceAtLeast(1f)

                val points = samples.map {
                    val x = axisPadding + (it.secondsFromStart / timeRange) * (width - axisPadding)
                    val y =
                        (height - axisPadding) - ((it.bpm - minBpm) / bpmRange) * (height - axisPadding)
                    Offset(x, y)
                }

                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = getHeartRateZoneColor(samples[i].bpm),
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 4f
                    )
                }
            }
        }
        Text(
            text = "BPM",
            style = TextStyle(fontSize = 12.sp),
            modifier = Modifier.Companion
                .align(Alignment.Companion.TopStart)
                .padding(start = 4.dp)
        )
        Text(
            text = "Seconds",
            style = TextStyle(fontSize = 12.sp),
            modifier = Modifier.Companion
                .align(Alignment.Companion.BottomEnd)
                .padding(end = 4.dp, bottom = 8.dp)
        )
    }
}

fun getHeartRateZoneColor(bpm: Int): Color = when {
    bpm < 100 -> Color.Blue      // Resting
    bpm < 120 -> Color.Green     // Fat Burn
    bpm < 150 -> Color.Yellow    // Cardio
    else -> Color.Red            // Peak
}
