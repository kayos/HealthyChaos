package com.kayos.healthykayos
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kayos.healthykayos.doubles.SensorStub
import com.kayos.healthykayos.sensor.IHeartRateSensor
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
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
                )
            )
        }

        composeTestRule
            .onNodeWithText("Start Recording")
            .isDisplayed()

        composeTestRule
            .onNodeWithText("Stop Recording")
            .assertDoesNotExist()
    }

//    @Test
//    fun recordingsScreen_whenStartClicked_StartsRecording() {
//        composeTestRule.setContent {
//            RecordingsScreen(
//                sensor = SensorStub(
//                    heartRate = MutableStateFlow(null),
//                    recordings = MutableStateFlow(emptyList())
//                )
//            )
//        }
//
//    }

    @Test
    fun recordingsScreen_whenRecording_onlyStopRecordingAvailable() {
        composeTestRule.setContent {
            RecordingsScreen(
                sensor = SensorStub(
                    heartRate = MutableStateFlow(null),
                    recordings = MutableStateFlow(emptyList())
                )
            )
        }

        composeTestRule
            .onNodeWithTag("test-start-record-btn")
            .performClick()

        composeTestRule
            .onNodeWithTag("test-stop-record-btn")
            .isDisplayed()

        composeTestRule
            .onNodeWithTag("test-start-record-btn")
            .assertDoesNotExist()
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
                )
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
        val recording =   PolarOfflineRecordingEntry("1", 123, Date(), PolarBleApi.PolarDeviceDataType.HR)
        val mockData = mock<PolarOfflineRecordingData>()
        val mockSensor = mock<IHeartRateSensor> {
            on { recordings } doReturn MutableStateFlow(listOf(recording))
            on { downloadRecording(recording) } doReturn Single.just(mockData)
        }

        composeTestRule.setContent {
            RecordingsScreen(
                sensor = mockSensor
            )
        }

        composeTestRule
            .onNodeWithTag("test-recording-item-0-download-btn")
            .performClick()

        verify(mockSensor).downloadRecording(recording)
    }

    @Test
    fun recordingsScreen_onAvailableRecordings_deleteClicked_triggersDeletion() {
        val recording =   PolarOfflineRecordingEntry("1", 123, Date(), PolarBleApi.PolarDeviceDataType.HR)
        val mockSensor = mock<IHeartRateSensor> {
            on { recordings } doReturn MutableStateFlow(listOf(recording))
        }

        composeTestRule.setContent {
            RecordingsScreen(
                sensor = mockSensor
            )
        }

        composeTestRule
            .onNodeWithTag("test-recording-item-0-delete-btn")
            .performClick()

        verify(mockSensor).deleteRecording(recording)
    }

}