package com.kayos.healthykayos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.kayos.polar.Device
import org.junit.Rule
import org.junit.Test

class DeviceBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenDeviceNotConnected_ShowsTextToShowNoConnection() {
        composeTestRule.setContent {
            DeviceBar(
                device = null
            )
        }

        composeTestRule
            .onNodeWithTag("test-no-device-text")
            .assertIsDisplayed()
    }

    @Test
    fun whenDeviceNotConnected_hidesNotConnectedText() {
        composeTestRule.setContent {
            DeviceBar(
                device = Device("123", "name")
            )
        }

        composeTestRule
            .onNodeWithTag("test-no-device-text")
            .assertDoesNotExist()
    }
}