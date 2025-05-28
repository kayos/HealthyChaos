package com.kayos.polar

import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class DeviceTest {

    @Test
    fun getRecordingsFunctionality_WhenNotImplemented_ReturnNull() {
        val device = Device("123", "name")

        assertNull(device.getRecordingsFunctionality())
    }

    @Test
    fun getRecordingsFunctionality_WhenImplemented_ExposesFunctionality() {
        val device = RecordingDevice()

        assertNotNull(device.getRecordingsFunctionality())
    }

    @Test
    fun getStreamFunctionality_WhenNotImplemented_ReturnNull() {
        val device = Device("123", "name")

        assertNull(device.getStreamFunctionality())
    }

    @Test
    fun getStreamFunctionality_WhenImplemented_ExposesFunctionality() {
        val device = StreamingDevice()

        assertNotNull(device.getStreamFunctionality())
    }
}

class StreamingDevice : Device("123", "recording"), IStreamAPI {
    override fun startStream(): Flow<HeartRate> {
        return flowOf(HeartRate(Instant.now(), 12))
    }
}

class RecordingDevice : Device("123", "recording"), IRecordingsAPI{
    override fun listRecordings(): Flow<List<PolarOfflineRecordingEntry>> {
        TODO("Not yet implemented")
    }

    override suspend fun startRecording() {
        TODO("Not yet implemented")
    }

    override suspend fun stopRecording() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecording(entry: PolarOfflineRecordingEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun downloadRecording(recording: PolarOfflineRecordingEntry): PolarOfflineRecordingData {
        TODO("Not yet implemented")
    }

    override suspend fun isRecording(): Boolean {
        TODO("Not yet implemented")
    }

}