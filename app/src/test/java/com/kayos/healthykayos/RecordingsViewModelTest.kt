package com.kayos.healthykayos

import app.cash.turbine.test
import com.kayos.healthykayos.doubles.FailingRecorderDevice
import com.kayos.healthykayos.doubles.FailingSensorStub
import com.kayos.healthykayos.doubles.SensorStub
import com.kayos.healthykayos.doubles.StubRecorderDevice
import com.kayos.healthykayos.testutils.MainDispatcherRule
import com.kayos.polar.Device
import com.kayos.polar.DeviceManager
import com.kayos.polar.IHeartRateSensor
import com.kayos.polar.IRecordingsAPI
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
import org.robolectric.RobolectricTestRunner
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class RecordingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun startRecording_setsRecordingState() = runTest {
        val deviceManager = DeviceManager()
        deviceManager.notifyDeviceConnected(StubRecorderDevice())
        val viewModel = RecordingsViewModel(SensorStub(
            heartRate = MutableStateFlow(null)
        ),deviceManager)

        viewModel.uiState.test{
            awaitItem()
            viewModel.startRecording()

            assertEquals(RecordingState.Recording(), awaitItem().recordingStatus)
        }
    }

    @Test
    fun startRecording_whenFails_setsRecordingState() = runTest{
        val deviceManager = DeviceManager()
        deviceManager.notifyDeviceConnected(FailingRecorderDevice())
        val viewModel = RecordingsViewModel(FailingSensorStub(),deviceManager)

        viewModel.uiState.test{
            viewModel.startRecording()

            assertEquals(RecordingState.NotRecording(), awaitItem().recordingStatus)
        }
    }

    @Test
    fun stopRecording_setsRecordingState() = runTest{
        val deviceManager = DeviceManager()
        deviceManager.notifyDeviceConnected(StubRecorderDevice())
        val viewModel = RecordingsViewModel(SensorStub(
            heartRate = MutableStateFlow(null)
        ), deviceManager)

        viewModel.uiState.test{
            awaitItem()
            viewModel.startRecording()

            assertEquals(RecordingState.Recording(), awaitItem().recordingStatus)

            viewModel.stopRecording()

            assertEquals(RecordingState.NotRecording(), awaitItem().recordingStatus)
        }
    }

    @Test
    fun stopRecording_whenFails_setsRecordingState()= runTest{
        val deviceManager = DeviceManager()
        deviceManager.notifyDeviceConnected(FailingRecorderDevice())
        val viewModel = RecordingsViewModel(FailingSensorStub(), deviceManager)

        viewModel.uiState.test{
            awaitItem()
            viewModel.stopRecording()

            assertEquals(RecordingState.Recording(), awaitItem().recordingStatus)
        }
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
        val recordingsAPI = mock<IRecordingsAPI> {
            on { listRecordings() } doReturn flowOf(listOf(newerEntry, olderEntry))
            onBlocking { isRecording() } doReturn false
        }
        val device = mock<Device>{
            on { getRecordingsFunctionality() } doReturn recordingsAPI
        }
        val deviceManager = DeviceManager()
        deviceManager.notifyDeviceConnected(device)

        val viewModel = RecordingsViewModel(sensorStub, deviceManager)
        viewModel.uiState.test {
            assertEquals(listOf(olderEntry, newerEntry), awaitItem().recordings)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshRecordings_updatesRecordings() = runTest {
        val expected : List<PolarOfflineRecordingEntry> = listOf(
            PolarOfflineRecordingEntry("a/b", 1234, Date(), PolarBleApi.PolarDeviceDataType.ACC)
        )
        val sensorStub = mock<IHeartRateSensor> {
            on { listRecordings() } doReturn flowOf(emptyList()) doReturn flowOf(expected)
            onBlocking { isRecording() } doReturn false
        }
        val recordingsAPI = mock<IRecordingsAPI> {
            on { listRecordings() } doReturn flowOf(emptyList()) doReturn flowOf(
                expected
            )
            onBlocking { isRecording() } doReturn false
        }
        val device = mock<Device>{
            on { getRecordingsFunctionality() } doReturn recordingsAPI
        }
        val deviceManager = DeviceManager()
        deviceManager.notifyDeviceConnected(device)

        val viewModel = RecordingsViewModel(sensorStub, deviceManager)
        viewModel.uiState.test {
            assertEquals(emptyList<PolarOfflineRecordingEntry>(), awaitItem().recordings)

            viewModel.refreshRecordings()

            assertEquals(expected, awaitItem().recordings)

            cancelAndIgnoreRemainingEvents()
        }
    }
}