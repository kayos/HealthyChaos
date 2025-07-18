import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kayos.device.HrData
import com.kayos.device.RecordingData
import com.kayos.healthykayos.ActivitySummary
import com.kayos.healthykayos.HeartRateGraph
import java.util.Calendar


@Composable
fun ActivityScreen(recording: RecordingData.HeartRateRecording) {
    val samples = recording.data.samples
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Activity", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Text("Summary", style = MaterialTheme.typography.titleMedium)
        ActivitySummary(recording.data.samples, modifier = Modifier.fillMaxWidth())
        Text("Heart Rate", style = MaterialTheme.typography.titleMedium)
        HeartRateGraph(samples = samples)
    }
}


@Preview(showBackground = true)
@Composable
fun ActivityScreenPreview() {
    val samples = listOf(
        HrData.HrSample(bpm = 80, secondsFromStart = 0),
        HrData.HrSample(bpm = 115, secondsFromStart = 1),
        HrData.HrSample(bpm = 190, secondsFromStart = 2),
        HrData.HrSample(bpm = 140, secondsFromStart = 3)
    )
    val cal = Calendar.getInstance()

    val recording = RecordingData.HeartRateRecording(
        cal,
        HrData(samples)
    )

    ActivityScreen(recording = recording)
}