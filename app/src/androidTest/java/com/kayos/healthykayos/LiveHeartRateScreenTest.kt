package com.kayos.healthykayos

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kayos.healthykayos.doubles.SensorStub
import com.kayos.healthykayos.sensor.HeartRate
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class LiveHeartRateScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val heartRate = MutableStateFlow<HeartRate?>(null)

    @Test
    fun liveHeartRateScreen_WhenNoHeartRate_DoesNotShowHR() {
        val sensorSub = SensorStub(heartRate)
        val viewModel = LiveHeartRateViewModel(sensorSub)
        composeTestRule.setContent {
            LiveHeartRateScreen(
                viewModel = viewModel
            )
        }

        composeTestRule
            .onNodeWithTag("test-hr-text")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithTag("test-bpm-text")
            .assertDoesNotExist()
    }

    @Test
    fun liveHeartRateScreen_WhenHeartRate_ShowsHR() {
        val sensorSub = SensorStub(heartRate)
        val viewModel = LiveHeartRateViewModel(sensorSub)
        composeTestRule.setContent {
            LiveHeartRateScreen(
                viewModel = viewModel
            )
        }

        heartRate.value = HeartRate(Instant.now(), 72)

        composeTestRule
            .onNodeWithTag("test-hr-text")
            .assertTextEquals("72")
            .isDisplayed()

        composeTestRule
            .onNodeWithTag("test-bpm-text")
            .assertTextEquals("bpm")
    }
}