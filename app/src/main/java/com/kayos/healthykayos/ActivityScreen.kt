import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kayos.device.HrData
import com.kayos.device.RecordingData
import java.util.Calendar

@Composable
fun ActivityScreen(recording: RecordingData.HeartRateRecording) {
    val samples = recording.data.samples
    val maxBpm = samples.maxOfOrNull { it.bpm } ?: 0
    val minBpm = samples.minOfOrNull { it.bpm } ?: 0
    val maxTime = samples.maxOfOrNull { it.secondsFromStart } ?: 1L

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Heart Rate", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)) {
            if (samples.size > 1) {
                val width = size.width
                val height = size.height
                val timeRange = maxTime.toFloat()
                val bpmRange = (maxBpm - minBpm).toFloat().coerceAtLeast(1f)

                val points = samples.map {
                    val x = (it.secondsFromStart / timeRange) * width
                    val y = height - ((it.bpm - minBpm) / bpmRange) * height
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