package com.kayos.polar

import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import kotlinx.coroutines.flow.Flow
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

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