package com.kayos.healthykayos

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kayos.healthykayos.doubles.SensorStub
import com.kayos.healthykayos.sensor.IHeartRateSensor
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.io.Writer
import java.util.Calendar
import java.util.Date

@RunWith(AndroidJUnit4::class)
class RecordingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun recordingsScreen_whenNotRecording_onlyStartRecordingAvailable() {
        composeTestRule.setContent {
            RecordingsScreen(
                sensor = SensorStub(
                    heartRate = MutableStateFlow(null),
                    recordings = MutableStateFlow(emptyList())
                ),
                isRecording = RecordingState.NotRecording(),
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->}
            )
        }

        composeTestRule
            .onNodeWithText("Start Recording")
            .isDisplayed()

        composeTestRule
            .onNodeWithText("Stop Recording")
            .assertDoesNotExist()
    }

    @Test
    fun recordingsScreen_whenStartClicked_StartsRecording() {
       var recording = false
        composeTestRule.setContent {
            RecordingsScreen(
                sensor =  SensorStub(
                    heartRate = MutableStateFlow(null),
                    recordings = MutableStateFlow(emptyList())
                ),
                isRecording = RecordingState.NotRecording(),
                onStartRecordingClick = { recording = true },
                onStopRecordingClick = { recording = false },
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->}
            )
        }

        composeTestRule
            .onNodeWithTag("test-start-record-btn")
            .performClick()

        assertTrue(recording)
    }

    @Test
    fun recordingsScreen_whenRecording_onlyStopRecordingAvailable() {
        composeTestRule.setContent {
            RecordingsScreen(
                sensor =  SensorStub(
                    heartRate = MutableStateFlow(null),
                    recordings = MutableStateFlow(emptyList())
                ),
                isRecording = RecordingState.Recording(),
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->}
            )
        }

        composeTestRule
            .onNodeWithTag("test-stop-record-btn")
            .isDisplayed()

        composeTestRule
            .onNodeWithTag("test-start-record-btn")
            .assertDoesNotExist()
    }

    @Test
    fun recordingsScreen_whenStopClicked_StopsRecording() {
        var recording = true
        composeTestRule.setContent {
            RecordingsScreen(
                sensor =  SensorStub(
                    heartRate = MutableStateFlow(null),
                    recordings = MutableStateFlow(emptyList())
                ),
                isRecording = RecordingState.Recording(),
                onStartRecordingClick = { recording = true },
                onStopRecordingClick = { recording = false },
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->}
            )
        }

        composeTestRule
            .onNodeWithTag("test-stop-record-btn")
            .performClick()

        assertFalse(recording)
    }

    @Test
    fun recordingsScreen_refresh_listsAvailableRecordings() {
        val calendar = Calendar.getInstance()
        calendar.set(2025, Calendar.APRIL, 18)

        composeTestRule.setContent {
            RecordingsScreen(
                sensor = SensorStub(
                    heartRate = MutableStateFlow(null),
                    recordings = MutableStateFlow(listOf(
                        PolarOfflineRecordingEntry("1", 123, Date(), PolarBleApi.PolarDeviceDataType.HR),
                        PolarOfflineRecordingEntry("2", 456, Date(), PolarBleApi.PolarDeviceDataType.PRESSURE)
                    ))
                ),
                isRecording = RecordingState.NotRecording(),
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->}
            )
        }

        composeTestRule
            .onNodeWithTag("test-recording-item-0")
            .isDisplayed()

        composeTestRule
            .onNodeWithTag("test-recording-item-1")
            .isDisplayed()
    }

    @Test
    fun recordingsScreen_onAvailableRecordings_downloadClicked_startsDownload() {
        var downloading = false
        composeTestRule.setContent {
            RecordingsScreen(
                sensor = SensorStub(
                    heartRate = MutableStateFlow(null),
                    recordings = MutableStateFlow(listOf(
                        PolarOfflineRecordingEntry("1", 123, Date(), PolarBleApi.PolarDeviceDataType.HR)
                    ))
                ),
                isRecording = RecordingState.NotRecording(),
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->
                    downloading = true}
            )
        }

        composeTestRule
            .onNodeWithTag("test-recording-item-0-download-btn")
            .performClick()

        assertTrue(downloading)
    }

    @Test
    fun recordingsScreen_onAvailableRecordings_deleteClicked_triggersDeletion() {
        val recording =   PolarOfflineRecordingEntry("1", 123, Date(), PolarBleApi.PolarDeviceDataType.HR)
        val mockSensor = mock<IHeartRateSensor> {
            on { recordings } doReturn MutableStateFlow(listOf(recording))
        }

        composeTestRule.setContent {
            RecordingsScreen(
                sensor = mockSensor,
                isRecording = RecordingState.NotRecording(),
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->}
            )
        }

        composeTestRule
            .onNodeWithTag("test-recording-item-0-delete-btn")
            .performClick()

        verify(mockSensor).deleteRecording(recording)
    }

}