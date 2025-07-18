package com.kayos.polar

import com.kayos.device.RecordingData
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Calendar
import kotlin.test.assertFailsWith

class PolarDeviceTest {
    @Test
    fun hasRecordingsFunctionality() {
        val api = mock<PolarBleApi>{}
        val device = PolarDevice("123", "name", api)

        assertNotNull(device.getRecordingsFunctionality())
    }

    @Test
    fun hasStreamFunctionality() {
        val api = mock<PolarBleApi>{}
        val device = PolarDevice("123", "name", api)

        assertNotNull(device.getStreamFunctionality())
    }


    @Test
    fun downloadRecording_downloads() = runBlocking {
        val mockApi = mock<PolarBleApi> {}
        val mockRecordingEntry = mock<PolarOfflineRecordingEntry> {}
        val startTime = Calendar.getInstance().apply { timeInMillis = 1000L }
        val emptyHrRecording = PolarOfflineRecordingData.HrOfflineRecording(PolarHrData(emptyList()), startTime)
        whenever(mockApi.getOfflineRecord("123", mockRecordingEntry)).thenReturn(
            Single.just(emptyHrRecording))

        val device = PolarDevice("123", "Test Device", mockApi)
        val result = device.downloadRecording(mockRecordingEntry)

        assertNotNull(result)
        assert(result is RecordingData.HeartRateRecording)
    }

    @Test
    fun downloadRecording_whenDownloadError_throws() {
        runBlocking {
            val mockApi = mock<PolarBleApi> {}
            val mockRecordingEntry = mock<PolarOfflineRecordingEntry> {}
            whenever(
                mockApi.getOfflineRecord(
                    "123",
                    mockRecordingEntry
                )
            ).thenReturn(Single.error(Exception("Download error")))
            val device = PolarDevice("123", "Test Device", mockApi)

            assertFailsWith<Exception> {
                device.downloadRecording(mockRecordingEntry)
            }
        }
    }
}