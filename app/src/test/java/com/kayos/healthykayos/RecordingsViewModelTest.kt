package com.kayos.healthykayos

import app.cash.turbine.test
import com.kayos.healthykayos.doubles.FailingSensorStub
import com.kayos.healthykayos.doubles.SensorStub
import com.kayos.healthykayos.testutils.MainDispatcherRule
import com.kayos.polar.IHeartRateSensor
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.Calendar
import java.util.Date
import org.robolectric.RobolectricTestRunner;

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class RecordingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun startRecording_setsRecordingState() {
        val viewModel = RecordingsViewModel(SensorStub(
            heartRate = MutableStateFlow(null)
        ))

        viewModel.startRecording()

        assertEquals(RecordingState.Recording(), viewModel.recordingState.value)
    }

    @Test
    fun startRecording_whenFails_setsRecordingState() {
        val viewModel = RecordingsViewModel(FailingSensorStub())

        viewModel.startRecording()

        assertEquals(RecordingState.NotRecording(), viewModel.recordingState.value)
    }

    @Test
    fun stopRecording_setsRecordingState() {
        val viewModel = RecordingsViewModel(SensorStub(
            heartRate = MutableStateFlow(null)
        ))
        viewModel.startRecording()

        viewModel.stopRecording()

        assertEquals(RecordingState.NotRecording(), viewModel.recordingState.value)
    }

    @Test
    fun stopRecording_whenFails_setsRecordingState() {
        val viewModel = RecordingsViewModel(FailingSensorStub())

        viewModel.stopRecording()

        assertEquals(RecordingState.Recording(), viewModel.recordingState.value)
    }

    @Test
    fun recordings_initialisesToExistingRecordings_sortedAscendingByDate() = runTest {
        val calendar = Calendar.getInstance()
        calendar.set(2022, Calendar.APRIL, 3)
        val olderEntry = PolarOfflineRecordingEntry("some/place", 4567, calendar.time, PolarBleApi.PolarDeviceDataType.PPG)
        calendar.set(2025, Calendar.MAY, 1)
        val newerEntry = PolarOfflineRecordingEntry("different/place", 678, calendar.time , PolarBleApi.PolarDeviceDataType.HR)

        val sensorStub = mock<IHeartRateSensor>{
            on { listRecordings() } doReturn flowOf(listOf(newerEntry, olderEntry))
            onBlocking { isRecording() } doReturn false
        }

        val viewModel = RecordingsViewModel(sensorStub)
        viewModel.recordings.test {
            assertEquals(listOf(olderEntry, newerEntry), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refresh_updatesRecordings() = runTest {
        val expected : List<PolarOfflineRecordingEntry> = listOf(
            PolarOfflineRecordingEntry("a/b", 1234, Date(), PolarBleApi.PolarDeviceDataType.ACC)
        )
        val sensorStub = mock<IHeartRateSensor> {
            on { listRecordings() } doReturn flowOf(emptyList()) doReturn flowOf(expected)
            onBlocking { isRecording() } doReturn false
        }

        val viewModel = RecordingsViewModel(sensorStub)
        viewModel.recordings.test {
            assertEquals(emptyList<PolarOfflineRecordingEntry>(), awaitItem())

            viewModel.refresh()

            assertEquals(expected, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}