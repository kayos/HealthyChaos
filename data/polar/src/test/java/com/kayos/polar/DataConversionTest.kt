import com.kayos.device.RecordingData
import com.kayos.polar.convert
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarTemperatureData
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class DataConversionTest {

    @Test
    fun convert_handleHrRecording(){
        val startTime = Calendar.getInstance().apply { timeInMillis = 1000L }
        val recordingData : PolarOfflineRecordingData = PolarOfflineRecordingData.HrOfflineRecording(
            startTime = startTime,
            data = PolarHrData(emptyList())
        )

        val result = recordingData.convert()

        assert(result is RecordingData.HeartRateRecording)
    }

    @Test
    fun convert_handlesUnknownRecording() {
        val startTime = Calendar.getInstance().apply { timeInMillis = 1000L }
        val unknownRecording = PolarOfflineRecordingData.SkinTemperatureOfflineRecording(
            PolarTemperatureData(emptyList()), startTime)

        val result = unknownRecording.convert()

        assertEquals(startTime, result.startTime)
        assert(result is RecordingData.UnknownRecording)
    }

    @Test
    fun convert_whenHr_HrOfflineRecordingToHeartRateRecording() {
        val samples = listOf(
            PolarHrData.PolarHrSample(80, 81, 0, emptyList(), false, false, false),
            PolarHrData.PolarHrSample(85, 81, 0, emptyList(), false, false, false),
            PolarHrData.PolarHrSample(90, 81, 0, emptyList(), false, false, false),
        )
        val startTime = Calendar.getInstance().apply { timeInMillis = 1000L }
        val hrOfflineRecording = PolarOfflineRecordingData.HrOfflineRecording(
            startTime = startTime,
            data = PolarHrData(samples)
        )

        val result = hrOfflineRecording.convert()

        assertEquals(startTime, result.startTime)
        assertEquals(3, result.data.samples.size)
        assertEquals(80, result.data.samples[0].bpm)
        assertEquals(0, result.data.samples[0].secondsFromStart)
        assertEquals(85, result.data.samples[1].bpm)
        assertEquals(1, result.data.samples[1].secondsFromStart)
        assertEquals(90, result.data.samples[2].bpm)
        assertEquals(2, result.data.samples[2].secondsFromStart)
    }

    @Test
    fun convert_whenHr_handlesEmptySamplesList() {
        val startTime = Calendar.getInstance().apply { timeInMillis = 1000L }
        val hrOfflineRecording = PolarOfflineRecordingData.HrOfflineRecording(
            startTime = startTime,
            data = PolarHrData(emptyList())
        )

        val result = hrOfflineRecording.convert()

        assertEquals(startTime, result.startTime)
        assertEquals(0, result.data.samples.size)
    }

    @Test
    fun convert_whenHr_handlesSingleSample() {
        val samples = listOf(
            PolarHrData.PolarHrSample(75, 40, 0, emptyList(), false, false, false),
        )
        val startTime = Calendar.getInstance().apply { timeInMillis = 2000L }
        val hrOfflineRecording = PolarOfflineRecordingData.HrOfflineRecording(
            startTime = startTime,
            data = PolarHrData(samples)
        )

        val result = hrOfflineRecording.convert()

        assertEquals(startTime, result.startTime)
        assertEquals(1, result.data.samples.size)
        assertEquals(75, result.data.samples[0].bpm)
        assertEquals(0, result.data.samples[0].secondsFromStart)
    }
}