package com.kayos.healthykayos

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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
                recordings = emptyList(),
                isRecording = RecordingState.NotRecording(),
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->},
                onDeleteClick = {},
                onRefreshClick = {}
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
                recordings = emptyList(),
                isRecording = RecordingState.NotRecording(),
                onStartRecordingClick = { recording = true },
                onStopRecordingClick = { recording = false },
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->},
                onDeleteClick = {},
                onRefreshClick = {}
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
                recordings = emptyList(),
                isRecording = RecordingState.Recording(),
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->},
                onDeleteClick = {},
                onRefreshClick = {}
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
                recordings = emptyList(),
                isRecording = RecordingState.Recording(),
                onStartRecordingClick = { recording = true },
                onStopRecordingClick = { recording = false },
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->},
                onDeleteClick = {},
                onRefreshClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("test-stop-record-btn")
            .performClick()

        assertFalse(recording)
    }

    @Test
    fun recordingsScreen_displaysAvailableRecordings() {
        val calendar = Calendar.getInstance()
        calendar.set(2025, Calendar.APRIL, 18)

        composeTestRule.setContent {
            RecordingsScreen(
                recordings = listOf(
                    PolarOfflineRecordingEntry("1", 123, Date(), PolarBleApi.PolarDeviceDataType.HR),
                    PolarOfflineRecordingEntry("2", 456, Date(), PolarBleApi.PolarDeviceDataType.PRESSURE),
                ),
                isRecording = RecordingState.NotRecording(),
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->},
                onDeleteClick = {},
                onRefreshClick = {}
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
    fun recordingsScreen_whenRefreshClicked_refreshesRecordings() {
        var refresh = false;
        composeTestRule.setContent {
            RecordingsScreen(
                recordings = emptyList<PolarOfflineRecordingEntry>(),
                isRecording = RecordingState.NotRecording(),
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->},
                onDeleteClick = {},
                onRefreshClick = { refresh = true }
            )
        }

        composeTestRule
            .onNodeWithTag("test-refresh-recordings-btn")
            .performClick()

        assertTrue(refresh)
    }

    @Test
    fun recordingsScreen_onAvailableRecordings_downloadClicked_startsDownload() {
        var downloading = false
        composeTestRule.setContent {
            RecordingsScreen(
                recordings = listOf(
                    PolarOfflineRecordingEntry("1", 123, Date(), PolarBleApi.PolarDeviceDataType.HR)
                ),
                isRecording = RecordingState.NotRecording(),
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->
                    downloading = true},
                onDeleteClick = {},
                onRefreshClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("test-recording-item-0-download-btn")
            .performClick()

        assertTrue(downloading)
    }

    @Test
    fun recordingsScreen_onAvailableRecordings_deleteClicked_triggersDeletion() {
        val expected = PolarOfflineRecordingEntry("2", 345, Date(), PolarBleApi.PolarDeviceDataType.PRESSURE)
        var actualDeleted : PolarOfflineRecordingEntry? = null
        composeTestRule.setContent {
            RecordingsScreen(
                recordings = listOf(
                    PolarOfflineRecordingEntry("1", 123, Date(), PolarBleApi.PolarDeviceDataType.HR),
                    expected
                ),
                isRecording = RecordingState.NotRecording(),
                onStartRecordingClick = {},
                onStopRecordingClick = {},
                onDownloadClick = {recording: PolarOfflineRecordingEntry, writer: Writer ->},
                onDeleteClick = { recording -> actualDeleted = recording},
                onRefreshClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("test-recording-item-1-delete-btn")
            .performClick()

        assertEquals(expected, actualDeleted)
    }

}