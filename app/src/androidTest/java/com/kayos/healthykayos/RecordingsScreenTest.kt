package com.kayos.healthykayos

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kayos.healthykayos.doubles.SensorStub
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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
            .onNodeWithTag("test-reocringd-item-0")
            .isDisplayed()

        composeTestRule
            .onNodeWithTag("test-reocringd-item-1")
            .isDisplayed()
    }
//
//    @Test
//    fun recordingsScreen_onAvailableRecordings_downloadClicked_startsDownload() {
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
//
//    @Test
//    fun recordingsScreen_onAvailableRecordings_deleteClicked_triggersDeletion() {
//
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

}