import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kayos.device.HrData
import com.kayos.device.RecordingData
import java.util.Calendar

@Composable
fun ActivityScreen(recording: RecordingData.HeartRateRecording) {
    val samples = recording.data.samples
    val maxBpm = samples.maxOfOrNull { it.bpm + 5 } ?: 0
    val minBpm = samples.minOfOrNull { it.bpm - 5 } ?: 0
    val maxTime = samples.maxOfOrNull { it.secondsFromStart } ?: 1L

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Activity", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Text("Heart Rate", style = MaterialTheme.typography.titleMedium)
        Box {
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)) {
                val width = size.width
                val height = size.height
                val axisPadding = 32f

                // Draw Y-axis
                drawLine(
                    color = Color.Gray,
                    start = Offset(axisPadding, 0f),
                    end = Offset(axisPadding, height - axisPadding),
                    strokeWidth = 3f
                )
                // Draw X-axis
                drawLine(
                    color = Color.Gray,
                    start = Offset(axisPadding, height - axisPadding),
                    end = Offset(width, height - axisPadding),
                    strokeWidth = 3f
                )

                if (samples.size > 1) {
                    val timeRange = maxTime.toFloat()
                    val bpmRange = (maxBpm - minBpm).toFloat().coerceAtLeast(1f)

                    val points = samples.map {
                        val x = axisPadding + (it.secondsFromStart / timeRange) * (width - axisPadding)
                        val y = (height - axisPadding) - ((it.bpm - minBpm) / bpmRange) * (height - axisPadding)
                        Offset(x, y)
                    }

                    for (i in 0 until points.size - 1) {
                        drawLine(
                            color = Color.Red,
                            start = points[i],
                            end = points[i + 1],
                            strokeWidth = 4f
                        )
                    }
                }
            }
            // Y-axis label
            Text(
                text = "BPM",
                style = TextStyle(fontSize = 12.sp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 4.dp)
            )
            // X-axis label
            Text(
                text = "Seconds",
                style = TextStyle(fontSize = 12.sp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 4.dp, bottom = 8.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ActivityScreenPreview() {
    val samples = listOf(
        HrData.HrSample(bpm = 80, secondsFromStart = 0),
        HrData.HrSample(bpm = 85, secondsFromStart = 1),
        HrData.HrSample(bpm = 90, secondsFromStart = 2),
        HrData.HrSample(bpm = 88, secondsFromStart = 3)
    )
    val cal = Calendar.getInstance()

    val recording = RecordingData.HeartRateRecording(
        cal,
        HrData(samples)
    )

    ActivityScreen(recording = recording)
}