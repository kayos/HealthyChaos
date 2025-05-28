package com.kayos.healthykayos

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kayos.device.HeartRate
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class LiveHeartRateScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun liveHeartRateScreen_WhenNoHeartRate_DoesNotShowHR() {
        composeTestRule.setContent {
            LiveHeartRateScreen(
                sample = null,
                onStartMonitoringClick = {},
                onStopMonitoringClick = {}
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
    fun liveHeartRateScreen_WhenNotMonitoring_StartMonitoringAvailable() {
        composeTestRule.setContent {
            LiveHeartRateScreen(
                sample = null,
                onStartMonitoringClick = {},
                onStopMonitoringClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("test-start-monitoring-btn")
            .assertTextEquals("Start monitoring")
            .isDisplayed()

        composeTestRule
            .onNodeWithTag("test-stop-monitoring-btn")
            .assertDoesNotExist()
    }

    @Test
    fun liveHeartRateScreen_whenStartMonitoringClicked_Starts() {
        var started = false
        composeTestRule.setContent {
            LiveHeartRateScreen(
                sample = null,
                onStartMonitoringClick = {started = true},
                onStopMonitoringClick = {}
            )
        }

        composeTestRule.onNodeWithTag("test-start-monitoring-btn").performClick()

        assertTrue(started)
    }

    @Test
    fun liveHeartRateScreen_WhenHeartRateMonitored_ShowsHR() {
        composeTestRule.setContent {
            LiveHeartRateScreen(
                sample =  HeartRate(Instant.now(), 72),
                onStartMonitoringClick = {},
                onStopMonitoringClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("test-hr-text")
            .assertTextEquals("72")
            .isDisplayed()

        composeTestRule
            .onNodeWithTag("test-bpm-text")
            .assertTextEquals("bpm")
            .isDisplayed()
    }

    fun liveHeartRateScreen_WhenMonitoringAndHeartRateMissing_ShowsEmptyBpmAndStopAvailable() {
        composeTestRule.setContent {
            LiveHeartRateScreen(
                sample =  null,
                onStartMonitoringClick = {},
                onStopMonitoringClick = {}
            )
        }

        composeTestRule.onNodeWithTag("test-start-monitoring-btn").performClick()

        composeTestRule
            .onNodeWithTag("test-hr-text")
            .assertTextEquals("")
            .isDisplayed()

        composeTestRule
            .onNodeWithTag("test-bpm-text")
            .assertTextEquals("bpm")
            .isDisplayed()

        composeTestRule
            .onNodeWithTag("test-stop-monitoring-btn")
            .isDisplayed()
    }

    @Test
    fun liveHeartRateScreen_whenMonitoring_MakesStopMonitoringAvailable() {
        composeTestRule.setContent {
            LiveHeartRateScreen(
                sample = null,
                onStartMonitoringClick = {},
                onStopMonitoringClick = {}
            )
        }

        composeTestRule.onNodeWithTag("test-start-monitoring-btn").performClick()

        composeTestRule
            .onNodeWithTag("test-start-monitoring-btn")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithTag("test-stop-monitoring-btn")
            .assertTextEquals("Stop monitoring")
            .isDisplayed()
    }

    @Test
    fun liveHeartRateScreen_whenStopMonitoringClicked_Stops() {
        var stopped = false
        composeTestRule.setContent {
            LiveHeartRateScreen(
                sample = null,
                onStartMonitoringClick = { stopped = false},
                onStopMonitoringClick = { stopped = true}
            )
        }
        composeTestRule.onNodeWithTag("test-start-monitoring-btn").performClick()

        composeTestRule.onNodeWithTag("test-stop-monitoring-btn").performClick()

        assertTrue(stopped)
    }
}